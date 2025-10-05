package com.bizmate.groupware.approval.service;

import com.bizmate.groupware.approval.dto.ApprovalDocumentsDto;
import com.bizmate.groupware.approval.dto.DocumentSearchRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;

public interface ApprovalDocumentsService {

    //임시저장
    ApprovalDocumentsDto draft(ApprovalDocumentsDto dto) throws JsonProcessingException;
    //상신(PENDING)
    ApprovalDocumentsDto submit(ApprovalDocumentsDto dto) throws JsonProcessingException;
    //승인
    ApprovalDocumentsDto approve(String docId, Long actorUserId, String comment);
    //반려
    ApprovalDocumentsDto reject(String docId, Long actorUserId, String reason);
    //논리삭제
    void logicalDelete(String docId, Long actorUserId, String reason);

    ApprovalDocumentsDto get(String docId);
    Page<ApprovalDocumentsDto> search(DocumentSearchRequestDto req);
}
