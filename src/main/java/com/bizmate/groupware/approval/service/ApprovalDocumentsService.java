package com.bizmate.groupware.approval.service;



import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.groupware.approval.domain.DocumentStatus;
import com.bizmate.groupware.approval.dto.ApprovalDocumentsDto;
import com.bizmate.groupware.approval.dto.DocumentSearchRequestDto;
import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.user.UserDTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ApprovalDocumentsService {

    /* ----------------------------- 작성/상신 ------------------------------ */
    ApprovalDocumentsDto draft(ApprovalDocumentsDto dto, UserDTO loginUser) throws JsonProcessingException;

    ApprovalDocumentsDto submit(ApprovalDocumentsDto dto, UserDTO loginUser) throws JsonProcessingException;

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

    ApprovalDocumentsDto resubmit(String docId, ApprovalDocumentsDto dto, UserDTO loginUser);
}
