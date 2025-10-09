package com.bizmate.groupware.approval.service;

import com.bizmate.groupware.approval.domain.DocumentStatus;
import com.bizmate.groupware.approval.dto.ApprovalDocumentsDto;
import com.bizmate.groupware.approval.dto.DocumentSearchRequestDto;
import com.bizmate.hr.domain.Departments;
import com.bizmate.hr.domain.Users;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ApprovalDocumentsService {

    /* ----------------------------- 작성/상신 ------------------------------ */
    ApprovalDocumentsDto draft(ApprovalDocumentsDto dto) throws JsonProcessingException;

    ApprovalDocumentsDto submit(ApprovalDocumentsDto dto) throws JsonProcessingException;

    /* ----------------------------- 결재/반려/삭제 ------------------------------ */
    ApprovalDocumentsDto approve(String docId, Long actorUserId, String comment);

    ApprovalDocumentsDto reject(String docId, Long actorUserId, String reason);

    void logicalDelete(String docId, Long actorUserId, String reason);

    /* ----------------------------- 조회 ------------------------------ */
    ApprovalDocumentsDto get(String docId);

    List<ApprovalDocumentsDto> findAllApprovals();

    List<ApprovalDocumentsDto> findMyApprovals(Long userId);

    Page<ApprovalDocumentsDto> search(DocumentSearchRequestDto req);

    /* ----------------------------- 필터별 조회 ------------------------------ */
    List<ApprovalDocumentsDto> findByDepartment(Departments department);

    List<ApprovalDocumentsDto> findByAuthor(Users author);

    List<ApprovalDocumentsDto> findByStatus(DocumentStatus status);
}
