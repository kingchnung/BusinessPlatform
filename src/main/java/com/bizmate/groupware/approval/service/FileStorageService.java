package com.bizmate.groupware.approval.service;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.ApprovalFileAttachment;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.groupware.approval.domain.ApprovalFileAttachment;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    ApprovalFileAttachment saveFile(MultipartFile file, ApprovalDocuments document);

    ApprovalFileAttachment saveFile(MultipartFile file, ApprovalDocuments document, UserDTO uploader);

    void deleteFile(String filePath);
}
