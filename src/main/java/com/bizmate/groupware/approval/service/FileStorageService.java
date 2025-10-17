package com.bizmate.groupware.approval.service;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.ApprovalFileAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    ApprovalFileAttachment saveFile(MultipartFile file, ApprovalDocuments document);

}
