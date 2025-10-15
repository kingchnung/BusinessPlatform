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
import com.bizmate.hr.security.UserPrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    public ApprovalDocumentsDto draft(ApprovalDocumentsDto dto, UserDTO loginUser) throws JsonProcessingException {
        log.info("📝 [임시저장 서비스 호출] 작성자={}, DTO={}", loginUser.getEmpName(), dto);

        // ✅ 작성자 정보 세팅
        dto.setUserId(loginUser.getUserId());
        dto.setAuthorName(loginUser.getEmpName());
        validateDraft(dto);

        // ✅ 부서 정보 보정 (DTO에 값이 없을 경우 자동조회)
        Long departmentId = dto.getDepartmentId();
        String departmentCode = dto.getDepartmentCode();

        if (departmentId == null || departmentCode == null || departmentCode.isBlank()) {
            UserEntity userEntity = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new VerificationFailedException("작성자(UserEntity)를 찾을 수 없습니다."));

            Department dept = userEntity.getEmployee() != null ? userEntity.getEmployee().getDepartment() : null;
            if (dept == null)
                throw new VerificationFailedException("부서 정보를 찾을 수 없습니다.");

            departmentId = dept.getDeptId();
            departmentCode = dept.getDeptCode();

            dto.setDepartmentId(departmentId);
            dto.setDepartmentCode(departmentCode);
        }

        // ✅ 문서번호 생성 (ApprovalIdGenerator 사용)
        String docNumber = approvalIdGenerator.generateNewId(departmentId, departmentCode);
        dto.setId(docNumber);
        dto.setFinalDocNumber(docNumber);

        // ✅ 엔티티 변환 및 저장
        ApprovalDocuments entity = mapDtoToEntity(dto, DocumentStatus.DRAFT);
        entity.markCreated(loginUser); // Auditing 기록

        ApprovalDocuments saved = approvalDocumentsRepository.save(entity);

        // ✅ 첨부파일 처리
        handleFileAttachments(dto, saved, loginUser);

        log.info("✅ 임시저장 완료: 문서ID={}", saved.getDocId());
        return mapEntityToDto(saved);
    }


    /* -------------------------------------------------------------
       ② 상신 (SUBMIT)
       ------------------------------------------------------------- */
    @Override
    @Transactional
    public ApprovalDocumentsDto submit(ApprovalDocumentsDto dto, UserDTO loginUser) throws JsonProcessingException {
        log.info("🚀 [상신 서비스 호출] 작성자={}, DTO={}", loginUser.getEmpName(), dto);
        log.info("🔑 submit() loginUser.username={}, userId={}", loginUser.getUsername(), loginUser.getUserId());

        // ✅ 작성자 정보 세팅
        dto.setUserId(loginUser.getUserId());
        dto.setAuthorName(loginUser.getEmpName());
        dto.setUsername(loginUser.getUsername());

        validateDraft(dto);

        // ✅ 부서 정보 확인
        Long departmentId = dto.getDepartmentId();
        String departmentCode = dto.getDepartmentCode();

        if (departmentId == null || departmentCode == null || departmentCode.isBlank()) {
            UserEntity userEntity = userRepository.findById(loginUser.getUserId())
                    .orElseThrow(() -> new VerificationFailedException("작성자(UserEntity)를 찾을 수 없습니다."));

            Department dept = userEntity.getEmployee() != null ? userEntity.getEmployee().getDepartment() : null;
            if (dept == null)
                throw new VerificationFailedException("부서 정보를 찾을 수 없습니다.");

            departmentId = dept.getDeptId();
            departmentCode = dept.getDeptCode();

            dto.setDepartmentId(departmentId);
            dto.setDepartmentCode(departmentCode);
        }

        // ✅ 3. 신규 vs 임시저장 구분
        boolean isDraft = "DRAFT".equalsIgnoreCase(dto.getStatus());
        ApprovalDocuments entity;

        if (isDraft && dto.getId() != null) {
            // ① 임시저장(DRAFT) → 상신(IN_PROGRESS)
            log.info("✏️ 임시저장 문서 상신 전환: {}", dto.getId());

            entity = approvalDocumentsRepository.findById(dto.getId())
                    .orElseThrow(() -> new VerificationFailedException("임시저장 문서를 찾을 수 없습니다."));

            entity.setStatus(DocumentStatus.IN_PROGRESS);
            entity.markUpdated(loginUser);
        } else {
            // ② 신규 상신
            String docNumber = approvalIdGenerator.generateNewId(departmentId, departmentCode);
            dto.setId(docNumber);
            dto.setFinalDocNumber(docNumber);

            log.info("🆕 신규 상신 생성: {}", docNumber);
            entity = mapDtoToEntity(dto, DocumentStatus.IN_PROGRESS);
            entity.markCreated(loginUser);
        }

        ApprovalDocuments saved = approvalDocumentsRepository.save(entity);

        // ✅ 첨부파일 처리
        handleFileAttachments(dto, saved, loginUser);

        log.info("✅ 상신 완료: 문서ID={}", saved.getDocId());
        return mapEntityToDto(saved);
    }

    /* -------------------------------------------------------------
   ✅ ③ 반려문서 재상신 (Resubmit)
   ------------------------------------------------------------- */
    @Override
    @Transactional
    public ApprovalDocumentsDto resubmit(String docId, ApprovalDocumentsDto dto, UserDTO loginUser) {
        log.info("🔁 [문서 재상신 시작] docId={}, 사번={}, 이름={}", docId, loginUser.getUsername(), loginUser.getEmpName());

        ApprovalDocuments document = approvalDocumentsRepository.findById(docId)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));

        // 1️⃣ 상태 검증: 반려 상태만 재상신 가능
        if (document.getStatus() != DocumentStatus.REJECTED)
            throw new VerificationFailedException("반려(REJECTED) 상태의 문서만 재상신할 수 있습니다.");

        // 2️⃣ 작성자 일치 검증 (사번 기준)
        if (!document.getAuthorUser().getUsername().equals(loginUser.getUsername())) {
            throw new VerificationFailedException("작성자만 재상신할 수 있습니다.");
        }

        // 3️⃣ 부서코드 검증 (변경 불가)
        if (!document.getDepartment().getDeptCode().equals(dto.getDepartmentCode())) {
            throw new VerificationFailedException("부서 정보가 일치하지 않습니다.");
        }

        // 4️⃣ 결재선 초기화
        List<ApproverStep> approvalLine = document.getApprovalLine();
        if (approvalLine == null || approvalLine.isEmpty())
            throw new VerificationFailedException("결재선 정보가 존재하지 않습니다.");

        // 모든 결재자 상태 초기화 (반려 이력은 유지하고 재시작할 수도 있음)
        List<ApproverStep> resetLine = approvalLine.stream()
                .map(step -> new ApproverStep(
                        step.order(),
                        step.approverId(), // ✅ 사번(username) 기준
                        step.approverName(),
                        Decision.PENDING, // 전부 대기 상태로 초기화
                        "", // 코멘트 초기화
                        null // 결정시각 초기화
                ))
                .toList();

        document.setApprovalLine(resetLine);
        document.setCurrentApproverIndex(0); // 첫 번째 결재자부터 다시 시작
        document.setRejectedBy(null);
        document.setRejectedReason(null);
        document.setRejectedDate(null);
        document.setRejectedEmpId(null);
        document.setStatus(DocumentStatus.IN_PROGRESS); // 상태 복귀

        // 5️⃣ 변경자 정보 업데이트
        document.markUpdated(loginUser);

        // 6️⃣ 저장 및 즉시 flush
        approvalDocumentsRepository.saveAndFlush(document);

        log.info("✅ 재상신 완료: 문서ID={}, 상태={}, 첫 결재자={}",
                docId,
                document.getStatus(),
                resetLine.get(0).approverName());

        return mapEntityToDto(document);
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

        if (!current.approverName().equals(loginUser.getEmpName()))
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
        log.info("🔴 [반려 처리] 문서ID={}, 반려자={}, 사유={}", docId, loginUser.getEmpName(), reason);

        ApprovalDocuments document = approvalDocumentsRepository.findById(docId)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));

        if (!document.canReject())
            throw new VerificationFailedException("진행 중 상태의 문서만 반려할 수 있습니다.");

        List<ApproverStep> line = document.getApprovalLine();
        if (line == null || line.isEmpty())
            throw new VerificationFailedException("결재선 정보가 존재하지 않습니다.");

        int idx = document.getCurrentApproverIndex();
        ApproverStep current = line.get(idx);

        if (!current.approverId().equals(loginUser.getUsername()))
            throw new VerificationFailedException("현재 결재 차례가 아닙니다.");

        // ✅ 반려 처리
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
        approvalDocumentsRepository.saveAndFlush(document);

        return mapEntityToDto(document);
    }


    /* -------------------------------------------------------------
       ⑤ 논리삭제 (DELETE)
       ------------------------------------------------------------- */
    @Override
    public void logicalDelete(String docId, UserDTO loginUser, String reason) {
        log.info("🗑️ [문서 삭제] 문서ID={}, 삭제자={}, 사유={}", docId, loginUser.getEmpName(), reason);

        ApprovalDocuments doc = approvalDocumentsRepository.findById(docId)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));

        if (!doc.isDeletable())
            throw new VerificationFailedException("DRAFT/REJECTED 상태만 삭제 가능합니다.");

        doc.markDeleted(loginUser, reason);
        approvalDocumentsRepository.save(doc);
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
    private void handleFileAttachments(ApprovalDocumentsDto dto, ApprovalDocuments saved, UserDTO loginUser) {
        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            List<FileAttachment> newAttachments = dto.getAttachments().stream()
                    .filter(a -> a.getId() == null)
                    .map(a -> a.toEntity(saved))
                    .toList();

            if (!newAttachments.isEmpty()) {
                fileAttachmentRepository.saveAll(newAttachments);
                log.info("📎 첨부파일 {}건 매핑 완료 (문서ID={})", newAttachments.size(), saved.getDocId());
            }
        } else {
            UserEntity uploader = userRepository.findById(loginUser.getUserId())
                    .orElseThrow(() -> new VerificationFailedException("업로더를 찾을 수 없습니다."));
            int linkedCount = fileAttachmentRepository.linkPendingFiles(saved, uploader);
            log.info("📎 임시 업로드 {}건 연결됨 (문서ID={})", linkedCount, saved.getDocId());
        }
    }

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
        log.info("🧾 [mapDtoToEntity] 시작: username={}, userId={}", dto.getUsername(), dto.getUserId());
        ApprovalDocuments entity = new ApprovalDocuments();

        // 기본 필드
        entity.setDocId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDocType(dto.getDocType());
        entity.setStatus(status);
        entity.setFinalDocNumber(dto.getFinalDocNumber());
        entity.setDocContent(dto.getDocContent());
        // ✅ 결재선 보정 (결재자 이름 자동 매핑)
        if (dto.getApprovalLine() != null && !dto.getApprovalLine().isEmpty()) {
            List<ApproverStep> fixedLine = dto.getApprovalLine().stream()
                    .map(step -> {
                        String approverUsername = step.approverId() != null ? step.approverId() : null;
                        String approverName = step.approverName();

                        // approverName이 비어있다면 DB에서 가져오기
                        if ((approverName == null || approverName.isBlank()) && approverUsername != null) {
                            approverName = userRepository.findByUsername(approverUsername)
                                    .map(UserEntity::getEmpName)
                                    .orElse("미등록 사용자");
                        }

                        // ✅ record는 불변이라 새 객체 생성 필요
                        return new ApproverStep(
                                step.order(),
                                approverUsername != null ? approverUsername : "-",
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

        //사용자 매핑
        UserEntity userEntity = null;

        // 1️⃣ username 우선 조회
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            userEntity = userRepository.findByUsername(dto.getUsername()).orElse(null);
            log.debug("🔍 findByUsername 결과: {}", userEntity);
        }

        // 2️⃣ fallback: username이 없거나 매칭 실패 시 userId로 조회
        if (userEntity == null && dto.getUserId() != null) {
            userEntity = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new VerificationFailedException("작성자(UserEntity)를 찾을 수 없습니다."));
        }

        // 3️⃣ 그래도 못 찾으면 예외 처리
        if (userEntity == null) {
            throw new VerificationFailedException("작성자(UserEntity) 정보를 확인할 수 없습니다.");
        }

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
                        userEntity.getUsername(),
                        userEntity.getPwHash() != null ? userEntity.getPwHash() : "N/A",
                        userEntity.getEmpName(),
                        true,
                        true,
                        userEntity.getEmail(),
                        null,
                        null,
                        List.of()

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
                .username(user != null ? user.getUsername() : null)
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
