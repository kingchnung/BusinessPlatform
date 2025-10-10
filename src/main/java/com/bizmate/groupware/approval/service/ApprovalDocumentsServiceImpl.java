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
import jakarta.validation.constraints.NotNull;
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

    /* -------------------------------------------------------------
       ① 임시저장 (DRAFT)
       ------------------------------------------------------------- */
    @Override
    public ApprovalDocumentsDto draft(ApprovalDocumentsDto dto) throws JsonProcessingException {
        log.info("📝 [임시저장 서비스 호출] 요청 DTO: {}", dto);

        validateDraft(dto);

        // ✅ 문서번호(PK) 생성
        String docNumber = generateDocumentNumber(dto.getDepartmentId());
        dto.setId(docNumber);
        dto.setFinalDocNumber(docNumber);

        // ✅ 문서 Entity 변환 및 저장
        ApprovalDocuments entity = mapDtoToEntity(dto, DocumentStatus.DRAFT);
        ApprovalDocuments saved = approvalDocumentsRepository.save(entity);

        // ✅ 첨부파일 처리
        saveAttachments(dto.getAttachments(), saved);

        log.info("✅ 임시저장 완료: {}", saved.getDocId());
        return mapEntityToDto(saved);
    }

    /* -------------------------------------------------------------
       ② 상신 (SUBMIT)
       ------------------------------------------------------------- */
    @Override
    public ApprovalDocumentsDto submit(ApprovalDocumentsDto dto) throws JsonProcessingException {
        log.info("🚀 [상신 서비스 호출] 요청 DTO: {}", dto);

        validateDraft(dto);

        String docNumber = (dto.getId() != null && !dto.getId().isEmpty())
                ? dto.getId()
                : generateDocumentNumber(dto.getDepartmentId());

        dto.setId(docNumber);
        dto.setFinalDocNumber(docNumber);

        ApprovalDocuments entity = mapDtoToEntity(dto, DocumentStatus.IN_PROGRESS);
        ApprovalDocuments saved = approvalDocumentsRepository.save(entity);

        saveAttachments(dto.getAttachments(), saved);

        log.info("✅ 문서 상신 완료: {}", saved.getDocId());
        return mapEntityToDto(saved);
    }

    /* -------------------------------------------------------------
       ③ 승인 (APPROVE)
       ------------------------------------------------------------- */
    @Override
    public ApprovalDocumentsDto approve(String docId, UserDTO loginUser) {
        ApprovalDocuments doc = approvalDocumentsRepository.findById(docId)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));

        if (doc.getStatus() != DocumentStatus.IN_PROGRESS)
            throw new VerificationFailedException("진행 중(IN_PROGRESS) 상태의 문서만 승인할 수 있습니다.");

        // ✅ 도메인 로직으로 이동
        doc.markApproved(loginUser);

        approvalDocumentsRepository.save(doc);
        log.info("🟢 승인 완료: 문서ID={}, 승인자={}", docId, loginUser.getEmpName());

        return mapEntityToDto(doc);
    }

    /* -------------------------------------------------------------
       ④ 반려 (REJECT)
       ------------------------------------------------------------- */
    @Override
    public ApprovalDocumentsDto reject(String docId, UserDTO loginUser, String reason) {
        ApprovalDocuments doc = approvalDocumentsRepository.findById(docId)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));

        if (doc.getStatus() != DocumentStatus.IN_PROGRESS)
            throw new VerificationFailedException("진행 중(IN_PROGRESS) 상태의 문서만 반려할 수 있습니다.");

        // ✅ 도메인 로직으로 이동
        doc.markRejected(loginUser, reason);

        approvalDocumentsRepository.save(doc);
        log.info("🔴 반려 완료: 문서ID={}, 반려자={}, 사유={}", docId, loginUser.getEmpName(), reason);

        return mapEntityToDto(doc);
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

        var attachments = attachmentDtos.stream()
                .map(dto -> FileAttachment.builder()
                        .document(document)
                        .originalName(dto.getOriginalName())
                        .storedName(dto.getStoredName())
                        .filePath(dto.getFilePath())
                        .fileSize(dto.getFileSize())
                        .contentType(dto.getContentType())
                        .build())
                .toList();

        fileAttachmentRepository.saveAll(attachments);
        log.info("📎 첨부파일 {}건 저장 완료 (문서 ID={})", attachments.size(), document.getDocId());
    }

    private void validateDraft(ApprovalDocumentsDto dto) {
        if (dto.getDepartmentId() == null)
            throw new VerificationFailedException("부서 ID는 필수입니다.");
        if (dto.getUserId() == null)
            throw new VerificationFailedException("작성자 ID는 필수입니다.");
        if (dto.getEmpId() == null)
            throw new VerificationFailedException("사번은 필수입니다.");
        if (dto.getDocType() == null)
            throw new VerificationFailedException("문서 유형은 필수입니다.");
    }

    private ApprovalDocuments mapDtoToEntity(ApprovalDocumentsDto dto, DocumentStatus status) {

        ApprovalDocuments entity = new ApprovalDocuments();

        // 1️⃣ 기본 필드
        entity.setDocId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDocType(dto.getDocType());
        entity.setStatus(status);
        entity.setFinalDocNumber(dto.getFinalDocNumber());
        entity.setDocContent(dto.getDocContent());
        entity.setApprovalLine(dto.getApprovalLine());
        entity.setCurrentApproverIndex(0);

        // 2️⃣ 부서 매핑
        Department dept = departmentRepository.findByDeptCode(dto.getDepartmentCode())
                .orElseThrow(() -> new VerificationFailedException("존재하지 않는 부서입니다."));
        entity.setDepartment(dept);

        // 3️⃣ UserEntity 로드 (작성자)
        UserEntity userEntity = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new VerificationFailedException("작성자(UserEntity)를 찾을 수 없습니다."));

        entity.setAuthorUser(userEntity); // ✅ USER_REF_ID 자동 세팅
        entity.setAuthorEmployee(userEntity.getEmployee()); // ✅ EMP_REF_ID 자동 세팅

        // 4️⃣ 역할 정보
        if (userEntity.getRoles() != null && !userEntity.getRoles().isEmpty()) {
            entity.setAuthorRole(userEntity.getRoles().iterator().next()); // 첫 번째 역할 기준
        }

        // 5️⃣ Auditing 필드
        entity.markCreated(
                new UserDTO(
                        userEntity.getUserId(),                                // Long
                        userEntity.getEmployee().getEmpId(),                   // Long
                        userEntity.getEmpName(),                               // String
                        userEntity.getEmployee().getDepartment().getDeptCode(), // String
                        userEntity.getUsername()
                )
        );

        // 6️⃣ DTO 편의 정보 세팅 (화면 표시용)
        dto.setAuthorName(userEntity.getEmpName());
        dto.setEmpId(userEntity.getEmployee().getEmpId());
        dto.setDepartmentId(userEntity.getEmployee().getDepartment().getDeptId());
        dto.setDepartmentName(userEntity.getEmployee().getDepartment().getDeptName());
        dto.setDepartmentCode(userEntity.getEmployee().getDepartment().getDeptCode());

        log.info("📋 DTO departmentCode={}, departmentId={}, userId={}, empId={}",
                dto.getDepartmentCode(), dto.getDepartmentId(), dto.getUserId(), dto.getEmpId());

        return entity;
    }

    private ApprovalDocumentsDto mapEntityToDto(ApprovalDocuments entity) {

        UserEntity user = entity.getAuthorUser();

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
                .attachments(entity.getAttachments() != null
                        ? entity.getAttachments().stream()
                        .map(a -> FileAttachmentDto.builder()
                                .id(a.getId())
                                .originalName(a.getOriginalName())
                                .filePath(a.getFilePath())
                                .fileSize(a.getFileSize())
                                .contentType(a.getContentType())
                                .build())
                        .toList()
                        : null)
                .build();
    }

    /* ✅ 문서번호 생성: HR-YYYYMMDD-001 */
    private String generateDocumentNumber(Long departmentId) {
        String deptCode = departmentRepository.findById(departmentId)
                .map(Department::getDeptCode)
                .orElse("UNK");

        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        long count = approvalDocumentsRepository.countByDepartment_DeptIdAndCreatedAtBetween(
                departmentId,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().plusDays(1).atStartOfDay()
        );

        String seq = String.format("%03d", count + 1);
        return deptCode + "-" + today + "-" + seq;
    }
}
