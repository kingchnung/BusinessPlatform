package com.bizmate.groupware.approval.service;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.common.exception.VerificationFailedException;
import com.bizmate.groupware.approval.domain.*;
import com.bizmate.groupware.approval.dto.ApprovalDocumentsDto;
import com.bizmate.groupware.approval.dto.DocumentSearchRequestDto;
import com.bizmate.groupware.approval.dto.FileAttachmentDto;
import com.bizmate.groupware.approval.repository.ApprovalDocumentsRepository;
import com.bizmate.groupware.approval.repository.FileAttachmentRepository;
import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.repository.DepartmentRepository;
import com.bizmate.hr.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApprovalDocumentsServiceImpl implements ApprovalDocumentsService {

    private final ApprovalDocumentsRepository approvalDocumentsRepository;
    private final DepartmentRepository departmentRepository;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final UserRepository userRepository;
    private final ApprovalIdGenerator approvalIdGenerator;

    /* -------------------------------------------------------------
       ① 임시저장 (DRAFT)
       ------------------------------------------------------------- */
    @Override
    @Transactional
    public ApprovalDocumentsDto draft(ApprovalDocumentsDto dto) throws JsonProcessingException {
        log.info("📝 [임시저장 서비스 호출] 요청 DTO: {}", dto);

        validateDraft(dto);

        // ✅ 부서 정보 확인
        Long departmentId = dto.getDepartmentId();
        String departmentCode = dto.getDepartmentCode();

        // 부서 ID나 코드가 DTO에 없으면 mapDtoToEntity 내부에서 자동으로 부서정보를 세팅하므로
        // 여기서도 미리 한 번 보완
        if (departmentId == null || departmentCode == null || departmentCode.isBlank()) {
            // 작성자의 부서를 기준으로 자동 조회
            UserEntity userEntity = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new VerificationFailedException("작성자(UserEntity)를 찾을 수 없습니다."));

            Department dept = userEntity.getEmployee() != null ? userEntity.getEmployee().getDepartment() : null;
            if (dept == null) {
                throw new VerificationFailedException("부서 정보를 찾을 수 없습니다.");
            }

            departmentId = dept.getDeptId();
            departmentCode = dept.getDeptCode();

            // DTO 보정
            dto.setDepartmentId(departmentId);
            dto.setDepartmentCode(departmentCode);
        }

        // ✅ ApprovalIdGenerator 사용 (동시성 보장 + DB 이어받기)
        String docNumber = approvalIdGenerator.generateNewId(departmentId, departmentCode);

        // ✅ 문서번호(PK) 세팅
        dto.setId(docNumber);
        dto.setFinalDocNumber(docNumber);

        // ✅ 문서 Entity 변환 및 저장
        ApprovalDocuments entity = mapDtoToEntity(dto, DocumentStatus.DRAFT);
        ApprovalDocuments saved = approvalDocumentsRepository.save(entity);

        // ✅ 첨부파일 처리

        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            List<FileAttachment> newAttachments = dto.getAttachments().stream()
                    .filter(a -> a.getId() == null) // ✅ 이미 DB에 존재하는 첨부파일은 제외
                    .map(a -> a.toEntity(saved))
                    .toList();

            if (!newAttachments.isEmpty()) {
                fileAttachmentRepository.saveAll(newAttachments);
                log.info("📎 신규 첨부파일 {}건이 문서 [{}]에 매핑 완료", newAttachments.size(), saved.getDocId());
            }
        } else {
            UserEntity uploader = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new VerificationFailedException("업로더를 찾을 수 없습니다."));
            int linkedCount = fileAttachmentRepository.linkPendingFiles(saved, uploader);
            log.info("📎 임시 업로드 파일 {}건이 문서 [{}]에 연결됨", linkedCount, saved.getDocId());
        }

        log.info("✅ 임시저장 완료: {}", saved.getDocId());

        return mapEntityToDto(saved);
    }


    /* -------------------------------------------------------------
       ② 상신 (SUBMIT)
       ------------------------------------------------------------- */
    @Override
    @Transactional
    public ApprovalDocumentsDto submit(ApprovalDocumentsDto dto) throws JsonProcessingException {
        log.info("🚀 [상신 서비스 호출] 요청 DTO: {}", dto);

        validateDraft(dto);

        // 1️⃣ 부서 정보 확인 (draft()와 동일 로직)
        Long departmentId = dto.getDepartmentId();
        String departmentCode = dto.getDepartmentCode();

        if (departmentId == null || departmentCode == null || departmentCode.isBlank()) {
            // 작성자 기준으로 부서정보 자동 조회
            UserEntity userEntity = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new VerificationFailedException("작성자(UserEntity)를 찾을 수 없습니다."));

            Department dept = userEntity.getEmployee() != null ? userEntity.getEmployee().getDepartment() : null;
            if (dept == null) {
                throw new VerificationFailedException("부서 정보를 찾을 수 없습니다.");
            }

            departmentId = dept.getDeptId();
            departmentCode = dept.getDeptCode();

            // DTO 보정
            dto.setDepartmentId(departmentId);
            dto.setDepartmentCode(departmentCode);
        }

        // 2️⃣ ApprovalIdGenerator를 통해 문서번호 생성
        String docNumber;
        if (dto.getId() != null && !dto.getId().isEmpty()) {
            // 이미 문서번호가 있으면 그대로 사용 (임시저장 후 상신 케이스)
            docNumber = dto.getId();
            log.info("📄 기존 임시저장 문서번호 사용: {}", docNumber);
        } else {
            // 새 문서 상신 시 자동 생성
            docNumber = approvalIdGenerator.generateNewId(departmentId, departmentCode);
            log.info("📄 신규 문서번호 생성: {}", docNumber);
        }

        // 3️⃣ DTO에 문서번호 반영
        dto.setId(docNumber);
        dto.setFinalDocNumber(docNumber);

        // 4️⃣ 문서 Entity 변환 및 저장 (상태: 진행중)
        ApprovalDocuments entity = mapDtoToEntity(dto, DocumentStatus.IN_PROGRESS);
        ApprovalDocuments saved = approvalDocumentsRepository.save(entity);

        // 5️⃣ 첨부파일 처리
        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            List<FileAttachment> newAttachments = dto.getAttachments().stream()
                    .filter(a -> a.getId() == null) // ✅ 이미 DB에 존재하는 첨부파일은 제외
                    .map(a -> a.toEntity(saved))
                    .toList();

            if (!newAttachments.isEmpty()) {
                fileAttachmentRepository.saveAll(newAttachments);
                log.info("📎 신규 첨부파일 {}건이 문서 [{}]에 매핑 완료", newAttachments.size(), saved.getDocId());
            }
        } else {
            UserEntity uploader = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new VerificationFailedException("업로더를 찾을 수 없습니다."));
            int linkedCount = fileAttachmentRepository.linkPendingFiles(saved, uploader);
            log.info("📎 임시 업로드 파일 {}건이 문서 [{}]에 연결됨", linkedCount, saved.getDocId());
        }

        log.info("✅ 문서 상신 완료: {}", saved.getDocId());

        return mapEntityToDto(saved);
    }


    /* -------------------------------------------------------------
   ✅ ③ 승인 (APPROVE) - 결재선 순서 기반 다단계 승인
   ------------------------------------------------------------- */
    @Override
    public ApprovalDocumentsDto approve(String docId, UserDTO loginUser) {
        ApprovalDocuments document = approvalDocumentsRepository.findById(docId)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));

        if (document.getStatus() != DocumentStatus.IN_PROGRESS)
            throw new VerificationFailedException("진행 중(IN_PROGRESS) 상태의 문서만 승인할 수 있습니다.");

        List<ApproverStep> line = document.getApprovalLine();
        if (line == null || line.isEmpty())
            throw new VerificationFailedException("결재선 정보가 존재하지 않습니다.");

        // 🔹 변경점: 현재 결재자 순서(currentApproverIndex) 기반 승인자 검증
        int idx = document.getCurrentApproverIndex();
        ApproverStep current = line.get(idx);

        if (!current.approverId().equals(loginUser.getUserId()))
            throw new VerificationFailedException("현재 결재 차례가 아닙니다.");

        // 🔹 변경점: 승인 처리 및 결재선 상태 갱신
        ApproverStep approved = new ApproverStep(
                current.order(),
                current.approverId(),
                current.approverName(),
                Decision.APPROVED,
                "", // 코멘트 없음
                LocalDateTime.now()
        );
        line.set(idx, approved);

        document.setApprovalLine(line);
        document.setApprovedBy(loginUser.getEmpName());
        document.setApprovedEmpId(loginUser.getEmpId());
        document.setApprovedDate(LocalDateTime.now());

        // 🔹 변경점: 다음 결재자 존재 여부에 따라 상태 및 인덱스 이동
        if (idx + 1 < line.size()) {
            document.setCurrentApproverIndex(idx + 1);
            document.setStatus(DocumentStatus.IN_PROGRESS);
            log.info("🟢 {} 승인 완료 → 다음 결재자 대기 (idx={})", loginUser.getEmpName(), idx + 1);
        } else {
            document.setStatus(DocumentStatus.APPROVED);
            log.info("✅ 모든 결재자 승인 완료 → 문서 최종 승인됨");
        }

        document.markUpdated(loginUser);

        // 🔹 변경점: 즉시 DB 반영 (Dirty Checking 방지)
        approvalDocumentsRepository.saveAndFlush(document);

        return mapEntityToDto(document);
    }

    /* -------------------------------------------------------------
       ④ 반려 (REJECT)
       ------------------------------------------------------------- */
    /* -------------------------------------------------------------
   ✅ ④ 반려 (REJECT) - 결재선 순서 기반 반려 처리
   ------------------------------------------------------------- */
    @Override
    public ApprovalDocumentsDto reject(String docId, UserDTO loginUser, String reason) {
        ApprovalDocuments document = approvalDocumentsRepository.findById(docId)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));

        if (!document.canReject())
            throw new VerificationFailedException("진행 중(IN_PROGRESS) 상태의 문서만 반려할 수 있습니다.");

        List<ApproverStep> line = document.getApprovalLine();
        if (line == null || line.isEmpty())
            throw new VerificationFailedException("결재선 정보가 존재하지 않습니다.");

        int idx = document.getCurrentApproverIndex();
        ApproverStep current = line.get(idx);

        if (!current.approverId().equals(loginUser.getUserId()))
            throw new VerificationFailedException("현재 결재 차례가 아닙니다.");

        // 🔹 변경점: 반려 처리 시 reason null 방지
        ApproverStep rejected = new ApproverStep(
                current.order(),
                current.approverId(),
                current.approverName(),
                Decision.REJECTED,
                reason != null ? reason : "",
                LocalDateTime.now()
        );
        line.set(idx, rejected);

        document.setApprovalLine(line);
        document.setRejectedBy(loginUser.getEmpName());
        document.setRejectedEmpId(loginUser.getEmpId());
        document.setRejectedReason(reason);
        document.setRejectedDate(LocalDateTime.now());
        document.setStatus(DocumentStatus.REJECTED);

        document.markUpdated(loginUser);

        // 🔹 변경점: 즉시 DB flush (상태 즉시 반영)
        approvalDocumentsRepository.saveAndFlush(document);

        log.info("🔴 반려 완료: 문서={}, 반려자={}, 사유={}", docId, loginUser.getEmpName(), reason);
        return mapEntityToDto(document);
    }


    /* -------------------------------------------------------------
       ⑤ 논리삭제 (DELETE)
       ------------------------------------------------------------- */
    @Override
    public void logicalDelete(String docId, UserDTO loginUser, String reason) {
        ApprovalDocuments doc = approvalDocumentsRepository.findById(docId)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));

        if (!doc.isDeletable())
            throw new VerificationFailedException("DRAFT/REJECTED 상태만 삭제 가능합니다.");

        // ✅ 도메인 로직으로 이동
        doc.markDeleted(loginUser, reason);

        approvalDocumentsRepository.save(doc);
        log.info("🗑️ 논리삭제 완료: 문서ID={}, 삭제자={}, 사유={}", docId, loginUser.getEmpName(), reason);
    }

    /* -------------------------------------------------------------
       ⑥ 조회 관련
       ------------------------------------------------------------- */
    @Override
    @Transactional(readOnly = true)
    public ApprovalDocumentsDto get(String docId) {
        return approvalDocumentsRepository.findById(docId)
                .map(this::mapEntityToDto)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));
    }

    @Override
    public PageResponseDTO<ApprovalDocumentsDto> getPagedApprovals(PageRequestDTO req) {
        int page = req.getPage() - 1; // 0-based index
        int size = req.getSize();

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<ApprovalDocuments> result = approvalDocumentsRepository.findAll(pageable);

        List<ApprovalDocumentsDto> dtoList = result.getContent()
                .stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());

        return PageResponseDTO.<ApprovalDocumentsDto>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(req)
                .totalCount(result.getTotalElements())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalDocumentsDto> findMyApprovals(Long userId) {
        return approvalDocumentsRepository.findByAuthorUser_UserId(userId)
                .stream()
                .map(this::mapEntityToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalDocumentsDto> search(DocumentSearchRequestDto req) {
        PageRequest pageable = PageRequest.of(req.getPage(), req.getSize(), Sort.by("createdAt").descending());
        return approvalDocumentsRepository.findAll(pageable)
                .map(this::mapEntityToDto);
    }

    @Override
    public List<ApprovalDocumentsDto> findByDepartment(Department department) {
        return List.of();
    }

    @Override
    public List<ApprovalDocumentsDto> findByAuthor(UserEntity author) {
        return List.of();
    }

    @Override
    public List<ApprovalDocumentsDto> findByStatus(DocumentStatus status) {
        return List.of();
    }

    /* -------------------------------------------------------------
       ✅ 내부 유틸
       ------------------------------------------------------------- */
    private void saveAttachments(List<FileAttachmentDto> attachmentDtos, ApprovalDocuments document) {
        if (attachmentDtos == null || attachmentDtos.isEmpty()) {
            return;
        }

        List<FileAttachment> list = attachmentDtos.stream()
                .map(dto -> dto.toEntity(document))
                .toList();

        fileAttachmentRepository.saveAll(list);
        log.info("📎 첨부파일 {}건 저장 완료 (문서ID={})", list.size(), document.getDocId());
    }

    private void validateDraft(ApprovalDocumentsDto dto) {
        if (dto.getUserId() == null)
            throw new VerificationFailedException("작성자 ID는 필수입니다.");
        if (dto.getDocType() == null)
            throw new VerificationFailedException("문서 유형은 필수입니다.");
    }

    private ApprovalDocuments mapDtoToEntity(ApprovalDocumentsDto dto, DocumentStatus status) {
        ApprovalDocuments entity = new ApprovalDocuments();

        // 기본 필드
        entity.setDocId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDocType(dto.getDocType());
        entity.setStatus(status);
        entity.setFinalDocNumber(dto.getFinalDocNumber());
        entity.setDocContent(dto.getDocContent());
        entity.setApprovalLine(dto.getApprovalLine());
        // ✅ 결재선 보정 (결재자 이름 자동 매핑)
        if (dto.getApprovalLine() != null && !dto.getApprovalLine().isEmpty()) {
            List<ApproverStep> fixedLine = dto.getApprovalLine().stream()
                    .map(step -> {
                        String approverName = step.approverName();
                        Long approverId = step.approverId();

                        // approverName이 비어있다면 DB에서 가져오기
                        if ((approverName == null || approverName.isBlank()) && approverId != null) {
                            approverName = userRepository.findById(approverId)
                                    .map(UserEntity::getEmpName)
                                    .orElse("미등록 사용자");
                        }

                        // ✅ record는 불변이라 새 객체 생성 필요
                        return new ApproverStep(
                                step.order(),
                                approverId,
                                approverName,
                                step.decision(),
                                step.comment(),
                                step.decidedAt()
                        );
                    })
                    .toList();

            entity.setApprovalLine(fixedLine);
        } else {
            entity.setApprovalLine(List.of());
        }

        entity.setCurrentApproverIndex(0);

        // ✅ 작성자 조회
        UserEntity userEntity = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new VerificationFailedException("작성자(UserEntity)를 찾을 수 없습니다."));

        // ✅ 부서 자동 연결
        Department dept;
        if (dto.getDepartmentCode() != null) {
            dept = departmentRepository.findByDeptCode(dto.getDepartmentCode())
                    .orElseThrow(() -> new VerificationFailedException("존재하지 않는 부서입니다."));
        } else {
            dept = userEntity.getEmployee() != null ? userEntity.getEmployee().getDepartment() : null;
            if (dept == null) throw new VerificationFailedException("사용자에게 부서 정보가 연결되어 있지 않습니다.");
        }
        entity.setDepartment(dept);

        // ✅ 작성자/직원/역할 매핑
        entity.setAuthorUser(userEntity);
        entity.setAuthorEmployee(userEntity.getEmployee());
        if (userEntity.getRoles() != null && !userEntity.getRoles().isEmpty()) {
            entity.setAuthorRole(userEntity.getRoles().iterator().next());
        }

        // ✅ Auditing 필드
        entity.markCreated(
                new UserDTO(
                        userEntity.getUserId(),
                        userEntity.getEmployee().getEmpId(),
                        userEntity.getEmpName(),
                        dept.getDeptCode(),
                        userEntity.getUsername()
                )
        );

        // ✅ DTO 화면 표시용 세팅
        dto.setAuthorName(userEntity.getEmpName());
        dto.setEmpId(userEntity.getEmployee().getEmpId());
        dto.setDepartmentId(dept.getDeptId());
        dto.setDepartmentName(dept.getDeptName());
        dto.setDepartmentCode(dept.getDeptCode());

        log.info("📋 DTO departmentCode={}, departmentId={}, userId={}, empId={}",
                dto.getDepartmentCode(), dto.getDepartmentId(), dto.getUserId(), dto.getEmpId());

        return entity;
    }


    private ApprovalDocumentsDto mapEntityToDto(ApprovalDocuments entity) {

        UserEntity user = entity.getAuthorUser();
        List<FileAttachmentDto> attachments = fileAttachmentRepository.findByDocument_DocId(entity.getDocId())
                .stream()
                .map(FileAttachmentDto::fromEntity)
                .toList();

        return ApprovalDocumentsDto.builder()
                .id(entity.getDocId())
                .title(entity.getTitle())
                .docType(entity.getDocType())
                .status(entity.getStatus().name())
                .departmentId(entity.getDepartment() != null ? entity.getDepartment().getDeptId() : null)
                .departmentName(entity.getDepartment() != null ? entity.getDepartment().getDeptName() : null)
                .finalDocNumber(entity.getFinalDocNumber())
                .userId(user != null ? user.getUserId() : null)
                .authorName(user != null ? user.getEmpName() : "-")   // ✅ users.emp_name 사용
                .departmentCode(user != null ? user.getDeptCode() : null)
                .docContent(entity.getDocContent())
                .approvalLine(entity.getApprovalLine())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .attachments(attachments)
                .build();
    }
}
