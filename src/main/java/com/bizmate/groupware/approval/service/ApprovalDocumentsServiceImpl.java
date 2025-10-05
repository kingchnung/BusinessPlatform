package com.bizmate.groupware.approval.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.bizmate.common.exception.VerificationFailedException;
import com.bizmate.groupware.approval.domain.*;
import com.bizmate.groupware.approval.dto.ApprovalDocumentsDto;
import com.bizmate.groupware.approval.dto.DocumentSearchRequestDto;
import com.bizmate.groupware.approval.repository.ApprovalDocumentSpecs;
import com.bizmate.groupware.approval.repository.ApprovalDocumentsRepository;
import com.bizmate.groupware.approval.repository.ApprovalHistoryRepository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalDocumentsServiceImpl implements ApprovalDocumentsService {

    @PersistenceContext
    private EntityManager em;
    private final ApprovalDocumentsRepository approvalDocumentsRepository;
    private final ApprovalHistoryRepository historyRepository;
    private final ApprovalIdGenerator idGenerator;
    private final ObjectMapper objectMapper;
    private final NotificationPort notificationPort;



    /* ----------------------------- 작성/상신 ------------------------------ */

    //임시저장(DRAFT)
    @Override
    @Transactional
    public ApprovalDocumentsDto draft(ApprovalDocumentsDto dto) throws JsonProcessingException {
        validateDraft(dto);
        String customId = idGenerator.generateNewId(dto.getDepartmentCode()); //HR-YYYYMMDD-001
        ApprovalDocuments saved = approvalDocumentsRepository.save(
                mapDtoToEntity(dto, customId, DocumentStatus.DRAFT)
        );
        saveHistory(saved.getDocId(), dto.getUserId(), "CREATE", null);
        return mapEntityToDto(saved);
    }

    //상신(IN_PROGRESS) -> 결재 진행중
    @Override
    @Transactional
    public ApprovalDocumentsDto submit(ApprovalDocumentsDto dto) throws JsonProcessingException {

        validateDraft(dto);
        String customId = idGenerator.generateNewId(dto.getDepartmentCode());
        ApprovalDocuments saved = approvalDocumentsRepository.save(
                mapDtoToEntity(dto, customId, DocumentStatus.IN_PROGRESS)
        );
        saveHistory(saved.getDocId(), dto.getUserId(), "SUBMIT", null);

        //첫 결재자 알림 샘플(이메일 리스트를 별도 매핑으로 가져오는 방식)
        notificationPort.notifyUsers(List.of("first-approver@example.com"),
                "[전자결재] 결재 요청 : " + saved.getTitle(),
                "문서 ID : " + saved.getDocId());

        return mapEntityToDto(saved);
    }

    /* ----------------------------- 결재/반려/삭제 ------------------------------ */

    //승인
    @Override
    @Transactional
    public ApprovalDocumentsDto approve(String docId, Long actorUserId, String comment) {

        ApprovalDocuments doc = approvalDocumentsRepository.findById(docId)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));

        requireNotDeleted(doc);
        requirePending(doc);
        requireActorIsCurrentApprover(doc, actorUserId);

        //다음 결재자로 이동 또는 최종 승인
        List<ApproverStep> steps = readApprovers(doc);
        int idx = doc.getCurrentApproverIndex();

        ApproverStep cur = steps.get(idx);
        steps.set(idx, new ApproverStep(
           cur.order(), cur.approverId(), Decision.APPROVED, comment, LocalDateTime.now()
        ));
        doc.setApprovalLine(steps);

        List<ApproverStep> approvers = readApprovers(doc);
        int next = doc.getCurrentApproverIndex() + 1;
        int total = approvers.size();

        if (next < total) {
            doc.setCurrentApproverIndex(next);
            saveHistory(docId, actorUserId, "APPROVE", comment);
            //다음 결재자 알림
            notificationPort.notifyUsers(List.of("next-approver@example.com"),
                    "[전자결재] 다음 결재 요청 : " + doc.getTitle(),
                    "문서ID : " + doc.getDocId()
            );
        } else {
            //최종 승인
            doc.setStatus(DocumentStatus.APPROVED);
            doc.setCurrentApproverIndex(next);
            //최종 문서 번호 채번(예 : 시퀀스/규칙)
            doc.setFinalDocNumber(generateFinalNumber(doc));
            saveHistory(docId, actorUserId, "FINAL_APPROVE", comment);
            //기안자/결재자/열람자 전체 알림
            notificationPort.notifyUsers(List.of("author@example.com", "all-approvers@example.com"),
                    "[전자결재] 최종 승인 완료 : " + doc.getTitle(),
                    "문서번호 : " + doc.getFinalDocNumber());
        }

        return mapEntityToDto(doc);

    }

    //반려
    @Override
    @Transactional
    public ApprovalDocumentsDto reject(String docId, Long actorUserId, String reason) {

        if (reason == null || reason.isBlank()) {
            throw new VerificationFailedException("반려 사유를 입력해 주세요.");
        }

        ApprovalDocuments doc = approvalDocumentsRepository.findById(docId)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));
        requireNotDeleted(doc);
        requirePending(doc);
        requireActorIsCurrentApprover(doc, actorUserId);

        doc.setStatus(DocumentStatus.REJECTED);
        saveHistory(docId, actorUserId, "REJECT", reason);

        List<ApproverStep> steps = readApprovers(doc);
        int idx = doc.getCurrentApproverIndex();

        ApproverStep cur = steps.get(idx);
        steps.set(idx, new ApproverStep(
                cur.order(),
                cur.approverId(),
                Decision.REJECTED,
                reason,
                LocalDateTime.now()
        ));
        doc.setApprovalLine(steps);
        doc.setStatus(DocumentStatus.REJECTED);

        //기안자 알림
        notificationPort.notifyUsers(
                List.of("author@example.com"),
                "[전자결재] 문서 반려 : " + doc.getTitle(), "사유 : " + reason
        );

        return mapEntityToDto(doc);

    }

    /* 논리 삭제(삭제/폐기 처리) */
    @Override
    @Transactional
    public void logicalDelete(String docId, Long actorUserId, String reason) {
        ApprovalDocuments doc = approvalDocumentsRepository.findById(docId)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));

        if (!doc.isDeletable()) {
            throw new VerificationFailedException("DRAFT/REJECTED 상태만 삭제 가능합니다.");
        }
        doc.setStatus(DocumentStatus.DELETED);
        saveHistory(docId, actorUserId, "DELETE", reason);
    }


    /* ----------------------------- 조회/검색 ------------------------------ */

    // 단건 조회
    @Override
    @Transactional(readOnly = true)
    public ApprovalDocumentsDto get(String docId) {

        ApprovalDocuments doc = approvalDocumentsRepository.findById(docId)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));
        if (doc.getStatus() == DocumentStatus.DELETED) {
            throw new VerificationFailedException("삭제된 문서입니다.");
        }

        return mapEntityToDto(doc);
    }


    //검색/필터 (상태/키워드/기간 ...)
    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalDocumentsDto> search(DocumentSearchRequestDto req) {
        Pageable pageable = PageRequest.of(
                req.getPage(),
                req.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        DocumentStatus statusEnum = (req.getStatus() != null && !req.getStatus().isBlank())
                ? DocumentStatus.valueOf(req.getStatus())
                : null;

        DocumentType docTypeEnum = (req.getDocType() != null && !req.getDocType().isBlank())
                ? DocumentType.valueOf(req.getDocType())
                : null;

        LocalDateTime from = parseStart(req.getFromDate());
        LocalDateTime to = parseEnd(req.getToDate());

        Specification<ApprovalDocuments> spec = Specification.allOf(
                ApprovalDocumentSpecs.notDeleted(),
                ApprovalDocumentSpecs.hasStatus(statusEnum),
                ApprovalDocumentSpecs.titleContains(req.getKeyword()),
                ApprovalDocumentSpecs.createdBetween(from, to),
                ApprovalDocumentSpecs.hasDocType(docTypeEnum)
        );

        Page<ApprovalDocuments> page =
                approvalDocumentsRepository.findAll(spec, pageable);
        return page.map(this::mapEntityToDto);
    }

    /* ---------------------------------- 내부 유틸 ----------------------------------------*/

    private List<ApproverStep> normalizeSteps(List<ApproverStep> steps) {
        if (steps == null) return List.of();
        return steps.stream().map(s ->
                new ApproverStep(
                        s.order(),
                        s.approverId(),
                        s.decision() == null ? Decision.PENDING : s.decision(),
                        s.comment(),
                        s.decidedAt()
                )
        ).toList();
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new VerificationFailedException("JSON 직렬화 실패", e);
        }
    }

    private List<Map<String, Object>> toApproverList(String json) {
        if (json == null || json.isBlank()) return List.of();

        try {
            return objectMapper.readValue(
                    json,
                    new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {
                    }
            );
        } catch (Exception e) {
            throw new VerificationFailedException("결재선 JSON 파싱 실패", e);
        }
    }

    private Map<String, Object> toContentMap(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return objectMapper.readValue(
                    json,
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                    }
            );
        } catch (Exception e) {
            throw new VerificationFailedException("본문 JSON 파싱 실패", e);
        }
    }


    private void validateDraft(ApprovalDocumentsDto dto) {
        if (dto.getDepartmentCode() == null || dto.getDepartmentCode().isBlank())
            throw new VerificationFailedException("부서코드가 필요합니다.");
        if (dto.getApprovalLine() == null)
            throw new VerificationFailedException("결재선이 필요합니다.");
        if (dto.getDocType() == null) {
            throw new VerificationFailedException("문서 유형이 필요합니다.");
        }
    }

    // DTO -> Entity
    private ApprovalDocuments mapDtoToEntity(ApprovalDocumentsDto dto, String id, DocumentStatus status) {

        ApprovalDocuments documents = new ApprovalDocuments();
        documents.setDocId(id);
        documents.setTitle(dto.getTitle());
        documents.setDocType(dto.getDocType()); //enum
        documents.setStatus(status);
        documents.setCurrentApproverIndex(0);

        documents.setAuthorUserId(dto.getUserId());
        documents.setAuthorRoleId(dto.getRoleId());
        documents.setAuthorEmpId(dto.getEmpId());

        documents.setApprovalLine(normalizeSteps(dto.getApprovalLine()));
        documents.setDocContent(dto.getDocContent());

        return documents;
    }

    private ApprovalDocumentsDto mapEntityToDto(ApprovalDocuments entity) {
        var b = ApprovalDocumentsDto.builder()
                .id(entity.getDocId())
                .title(entity.getTitle())
                .docType(entity.getDocType())
                .status(entity.getStatus().name())
                .finalDocNumber(entity.getFinalDocNumber())
                .userId(entity.getAuthorUserId())
                .roleId(entity.getAuthorRoleId())
                .empId(entity.getAuthorEmpId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        List<ApproverStep> approvers = entity.getApprovalLine() == null
                ? List.of()
                : entity.getApprovalLine();
        b.approvalLine(approvers);

        if (entity.getCurrentApproverIndex() < approvers.size()) {
            b.currentApproverId(approvers.get(entity.getCurrentApproverIndex()).approverId());
        }

        b.docContent(entity.getDocContent() == null
                ? Map.of()
                : entity.getDocContent());

        return b.build();
    }

    private void saveHistory(String docId, Long actorUserId, String action, String comment) {
        historyRepository.save(ApprovalHistory.builder()
                .docId(docId)
                .actorUserId(actorUserId)
                .actionType(action)
                .actionComment(comment)
                .actionTimestamp(LocalDateTime.now())
                .build());
    }

    private void requireNotDeleted(ApprovalDocuments d) {
        if (d.getStatus() == DocumentStatus.DELETED)
            throw new VerificationFailedException("삭제된 문서입니다.");
    }

    private void requirePending(ApprovalDocuments d) {
        if (d.getStatus() != DocumentStatus.IN_PROGRESS)
            throw new VerificationFailedException("IN_PROGRESS 상태만 처리 가능합니다.");
    }

    private void requireActorIsCurrentApprover(ApprovalDocuments d, Long actorUserId) {
        List<ApproverStep> approvers = readApprovers(d);
        if (d.getCurrentApproverIndex() >= approvers.size())
            throw new VerificationFailedException("결재선 인덱스 오류");
        //여기서는 단순 비교(실서비스는 사용자 ID 매핑 필요)
        //예 : approverId가 사번/사용자ID와 매핑되도록 설계
        // approverId <-> 사용자 ID 매핑 검증 로직 추가해야함
        // approvers.get(idx).approverId() 와 actorUserId 매핑 검증
    }

    private List<ApproverStep> readApprovers(ApprovalDocuments d) {
        List<ApproverStep> list = d.getApprovalLine();
        return (list == null) ? List.of() : list;
    }

    private String generateFinalNumber(ApprovalDocuments d) {
        //예 : DOC-2025-000001(시퀀스 사용 권장)
        String year = String.valueOf(LocalDate.now().getYear());
        Long next = ((Number) em.createNativeQuery("select APPROVAL_DOCUMENTS_NO_SEQ.nextval from dual").getSingleResult()).longValue();
        return "DOC-" + year + "-" + String.format("%06d", next);
    }

    private LocalDateTime parseStart(String from) {
        return (from == null || from.isBlank())
                ? LocalDate.MIN.atStartOfDay()
                : LocalDate.parse(from).atStartOfDay();
    }

    private LocalDateTime parseEnd(String to) {
        return (to == null || to.isBlank())
                ? LocalDateTime.now()
                : LocalDate.parse(to).atTime(23, 59, 59);
    }

    private Specification<ApprovalDocuments> notDeleted() {
        return (root, q, cb) -> cb.notEqual(root.get("status"), DocumentStatus.DELETED);
    }

    private Specification<ApprovalDocuments> statusEquals(String status) {
        return (root, q, cb) -> cb.equal(root.get("status"), DocumentStatus.valueOf(status));
    }

    private Specification<ApprovalDocuments> keywordLike(String kw) {
        String like = "%" + kw + "%";
        return (root, q, cb) -> cb.or(
                cb.like(root.get("title"), like),
                cb.like(root.get("docContentJson"), like)
        );
    }

    private Specification<ApprovalDocuments> createdBetween(String from, String to) {
        LocalDateTime start = (from == null) ? LocalDate.MIN.atStartOfDay()
                : LocalDate.parse(from).atStartOfDay();
        LocalDateTime end = (to == null) ? LocalDateTime.now()
                : LocalDate.parse(to).atTime(23, 59, 59);

        return (root, q, cb) -> cb.between(root.get("createdAt"), start, end);
    }
}
