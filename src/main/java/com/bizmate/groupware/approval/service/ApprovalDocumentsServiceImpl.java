package com.bizmate.groupware.approval.service;

import com.bizmate.common.exception.VerificationFailedException;
import com.bizmate.groupware.approval.domain.*;
import com.bizmate.groupware.approval.dto.ApprovalDocumentsDto;
import com.bizmate.groupware.approval.dto.DocumentSearchRequestDto;
import com.bizmate.groupware.approval.dto.FileAttachmentDto;
import com.bizmate.groupware.approval.repository.ApprovalDocumentsRepository;
import com.bizmate.groupware.approval.repository.FileAttachmentRepository;
import com.bizmate.hr.domain.Departments;
import com.bizmate.hr.domain.Employees;
import com.bizmate.hr.domain.Roles;
import com.bizmate.hr.domain.Users;
import com.bizmate.hr.repository.DepartmentRepository;
import com.bizmate.hr.repository.EmployeeRepository;
import com.bizmate.hr.repository.RoleRepository;
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
import java.util.*;
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
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;

    /* ----------------------------- ① 임시저장 ------------------------------ */
    @Override
    public ApprovalDocumentsDto draft(ApprovalDocumentsDto dto) throws JsonProcessingException {
        log.info("📝 [임시저장 서비스] 요청: {}", dto);

        validateDraft(dto);

        // ✅ 문서번호(PK) 생성
        String docNumber = generateDocumentNumber(dto.getDepartmentId());
        dto.setId(docNumber);
        dto.setFinalDocNumber(docNumber);

        // ✅ 문서 Entity 생성 및 저장
        ApprovalDocuments entity = mapDtoToEntity(dto, DocumentStatus.DRAFT);
        ApprovalDocuments saved = approvalDocumentsRepository.save(entity);
        log.info("✅ 문서 임시저장 완료: {}", saved.getDocId());

        // ✅ 첨부파일 자동 연결
        saveAttachments(dto.getAttachments(), saved);

        return mapEntityToDto(saved);
    }

    /* ----------------------------- ② 상신 ------------------------------ */
    @Override
    public ApprovalDocumentsDto submit(ApprovalDocumentsDto dto) throws JsonProcessingException {
        log.info("📤 [상신 서비스] 요청: {}", dto);

        validateDraft(dto);

        // ✅ 기존 문서번호 유지 or 새로 생성
        String docNumber = (dto.getId() != null && !dto.getId().isEmpty())
                ? dto.getId()
                : generateDocumentNumber(dto.getDepartmentId());
        dto.setId(docNumber);
        dto.setFinalDocNumber(docNumber);

        ApprovalDocuments entity = mapDtoToEntity(dto, DocumentStatus.IN_PROGRESS);
        ApprovalDocuments saved = approvalDocumentsRepository.save(entity);
        log.info("✅ 문서 상신 완료: {}", saved.getDocId());

        // ✅ 첨부파일 자동 연결
        saveAttachments(dto.getAttachments(), saved);

        return mapEntityToDto(saved);
    }

    /* ----------------------------- ③ 승인 ------------------------------ */
    @Override
    public ApprovalDocumentsDto approve(String docId, Long actorUserId, String comment) {
        ApprovalDocuments doc = approvalDocumentsRepository.findById(docId)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));

        if (doc.getStatus() != DocumentStatus.IN_PROGRESS)
            throw new VerificationFailedException("진행 중(IN_PROGRESS) 상태의 문서만 승인할 수 있습니다.");

        doc.setStatus(DocumentStatus.APPROVED);
        doc.setUpdatedAt(LocalDateTime.now());
        doc.setUpdatedBy(actorUserId);

        approvalDocumentsRepository.save(doc);
        log.info("✅ 승인 완료: {}", docId);
        return mapEntityToDto(doc);
    }

    /* ----------------------------- ④ 반려 ------------------------------ */
    @Override
    public ApprovalDocumentsDto reject(String docId, Long actorUserId, String reason) {
        ApprovalDocuments doc = approvalDocumentsRepository.findById(docId)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));

        if (doc.getStatus() != DocumentStatus.IN_PROGRESS)
            throw new VerificationFailedException("진행 중(IN_PROGRESS) 상태의 문서만 반려할 수 있습니다.");

        doc.setStatus(DocumentStatus.REJECTED);
        doc.setUpdatedAt(LocalDateTime.now());
        doc.setUpdatedBy(actorUserId);

        approvalDocumentsRepository.save(doc);
        log.info("❌ 반려 처리 완료: {}", docId);
        return mapEntityToDto(doc);
    }

    /* ----------------------------- ⑤ 논리삭제 ------------------------------ */
    @Override
    public void logicalDelete(String docId, Long actorUserId, String reason) {
        ApprovalDocuments doc = approvalDocumentsRepository.findById(docId)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));

        if (!doc.isDeletable())
            throw new VerificationFailedException("DRAFT/REJECTED 상태만 삭제 가능합니다.");

        doc.setStatus(DocumentStatus.DELETED);
        doc.setUpdatedAt(LocalDateTime.now());
        doc.setUpdatedBy(actorUserId);

        approvalDocumentsRepository.save(doc);
        log.info("🗑️ 논리삭제 완료: {}", docId);
    }

    /* ----------------------------- ⑥ 단건조회 ------------------------------ */
    @Override
    @Transactional(readOnly = true)
    public ApprovalDocumentsDto get(String docId) {
        return approvalDocumentsRepository.findById(docId)
                .map(this::mapEntityToDto)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));
    }

    /* ----------------------------- ⑦ 전체조회 ------------------------------ */
    @Override
    @Transactional(readOnly = true)
    public List<ApprovalDocumentsDto> findAllApprovals() {
        return approvalDocumentsRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }

    /* ----------------------------- ⑧ 로그인 사용자 문서조회 ------------------------------ */
    @Override
    @Transactional(readOnly = true)
    public List<ApprovalDocumentsDto> findMyApprovals(Long userId) {
        Users author = findUser(userId);
        return approvalDocumentsRepository.findByAuthorUser(author).stream()
                .map(this::mapEntityToDto).toList();
    }

    /* ----------------------------- ⑨ 검색 ------------------------------ */
    @Override
    @Transactional(readOnly = true)
    public Page<ApprovalDocumentsDto> search(DocumentSearchRequestDto req) {
        PageRequest pageable = PageRequest.of(req.getPage(), req.getSize(), Sort.by("createdAt").descending());
        Page<ApprovalDocuments> page = approvalDocumentsRepository.findAll(pageable);
        return page.map(this::mapEntityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalDocumentsDto> findByDepartment(Departments department) {
        if (department == null) {
            throw new VerificationFailedException("부서 정보가 없습니다.");
        }

        return approvalDocumentsRepository.findByDepartment(department).stream()
                .map(this::mapEntityToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalDocumentsDto> findByAuthor(Users author) {
        if (author == null) {
            throw new VerificationFailedException("작성자 정보가 없습니다.");
        }

        return approvalDocumentsRepository.findByAuthorUser(author).stream()
                .map(this::mapEntityToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalDocumentsDto> findByStatus(DocumentStatus status) {
        if (status == null) {
            throw new VerificationFailedException("문서 상태가 지정되지 않았습니다.");
        }

        return approvalDocumentsRepository.findByStatus(status).stream()
                .map(this::mapEntityToDto)
                .toList();
    }

    /* -----------------------------  내부 유틸 ------------------------------ */

    private void saveAttachments(List<FileAttachmentDto> attachmentDtos, ApprovalDocuments document) {
        if (attachmentDtos == null || attachmentDtos.isEmpty()) {
            log.info("📎 첨부파일 없음");
            return;
        }

        List<FileAttachment> attachments = attachmentDtos.stream()
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

    private Departments findDepartment(@NotNull Long deptId) {
        return departmentRepository.findById(Math.toIntExact(deptId))
                .orElseThrow(() -> new VerificationFailedException("존재하지 않는 부서입니다."));
    }

    private Users findUser(@NotNull Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new VerificationFailedException("존재하지 않는 사용자입니다."));
    }

    private Roles findRole(Integer roleId) {
        return (roleId != null)
                ? roleRepository.findById(roleId).orElse(null)
                : null;
    }

    private Employees findEmployee(Long empId) {
        return (empId != null)
                ? employeeRepository.findById(empId).orElse(null)
                : null;
    }

    private ApprovalDocuments mapDtoToEntity(ApprovalDocumentsDto dto, DocumentStatus status) {
        ApprovalDocuments entity = new ApprovalDocuments();

        // ✅ UUID 제거 → PK는 generateDocumentNumber() 결과
        entity.setDocId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDocType(dto.getDocType());
        entity.setStatus(status);
        entity.setDepartment(findDepartment(dto.getDepartmentId()));
        entity.setAuthorUser(findUser(dto.getUserId()));
        entity.setAuthorRole(findRole(dto.getRoleId()));
        entity.setAuthorEmployee(findEmployee(dto.getEmpId()));
        entity.setApprovalLine(dto.getApprovalLine());
        entity.setDocContent(dto.getDocContent());
        entity.setCurrentApproverIndex(0);
        entity.setCreatedBy(dto.getUserId());
        entity.setUpdatedBy(dto.getUserId());

        return entity;
    }

    private ApprovalDocumentsDto mapEntityToDto(ApprovalDocuments entity) {
        return ApprovalDocumentsDto.builder()
                .id(entity.getDocId())
                .title(entity.getTitle())
                .docType(entity.getDocType())
                .status(entity.getStatus().name())
                .departmentId(entity.getDepartment() != null ? Long.valueOf(entity.getDepartment().getDeptId()) : null)
                .departmentName(entity.getDepartment() != null ? entity.getDepartment().getDeptName() : null)
                .finalDocNumber(entity.getFinalDocNumber())
                .userId(entity.getAuthorUser() != null ? entity.getAuthorUser().getUserId() : null)
                .roleId(entity.getAuthorRole() != null ? entity.getAuthorRole().getRoleId() : null)
                .empId(entity.getAuthorEmployee() != null ? entity.getAuthorEmployee().getEmpId() : null)
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
        String deptCode = departmentRepository.findById(Math.toIntExact(departmentId))
                .map(Departments::getDeptCode)
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
