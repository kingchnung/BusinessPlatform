package com.bizmate.groupware.service;

import com.bizmate.groupware.dto.ApprovalDocumentsDto;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ApprovalDocumentsService {
    public ApprovalDocumentsDto createNewDocument(ApprovalDocumentsDto dto) throws JsonProcessingException;
}
