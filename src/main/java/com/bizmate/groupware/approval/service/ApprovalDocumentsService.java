package com.bizmate.groupware.approval.service;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.groupware.approval.domain.DocumentStatus;
import com.bizmate.groupware.approval.dto.ApprovalDocumentsDto;
import com.bizmate.groupware.approval.dto.DocumentSearchRequestDto;
import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.security.UserPrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApprovalDocumentsService {

    /* ----------------------------- 작성/상신 ------------------------------ */
    ApprovalDocumentsDto draft(ApprovalDocumentsDto dto, UserDTO loginUser) throws JsonProcessingException;

    ApprovalDocumentsDto submit(ApprovalDocumentsDto dto, UserDTO loginUser) throws JsonProcessingException;

    /* -------------------------------------------------------------
   ✅ ③ 반려문서 재상신 (Resubmit)
   ------------------------------------------------------------- */
    @Transactional
    ApprovalDocumentsDto resubmit(String docId, ApprovalDocumentsDto dto, List<MultipartFile> files, UserDTO loginUser);

    /* ----------------------------- 결재/반려/삭제 ------------------------------ */
    ApprovalDocumentsDto approve(String docId, UserDTO loginUser);

    ApprovalDocumentsDto reject(String docId, UserDTO loginUser, String reason);

    void logicalDelete(String docId, UserDTO loginUser, String reason);

    /* ----------------------------- 조회 ------------------------------ */
    ApprovalDocumentsDto get(String docId);

    PageResponseDTO<ApprovalDocumentsDto> getPagedApprovals(PageRequestDTO pageRequestDTO);

    List<ApprovalDocumentsDto> findMyApprovals(Long userId);

    Page<ApprovalDocumentsDto> search(DocumentSearchRequestDto req);

    /* ----------------------------- 필터별 조회 ------------------------------ */
    List<ApprovalDocumentsDto> findByDepartment(Department department);

    List<ApprovalDocumentsDto> findByAuthor(UserEntity author);

    List<ApprovalDocumentsDto> findByStatus(DocumentStatus status);

    void restoreDocument(String docId);

    PageResponseDTO<ApprovalDocumentsDto> getPagedApprovalsByUser(PageRequestDTO pageRequestDTO, String username);

    @Transactional
    void forceApprove(String docId, UserPrincipal adminUser, String reason);

    @Transactional
    void forceReject(String docId, UserPrincipal adminUser, String reason);
}
