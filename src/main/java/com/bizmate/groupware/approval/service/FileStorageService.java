package com.bizmate.groupware.approval.service;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.FileAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    FileAttachment saveFile(MultipartFile file, ApprovalDocuments document);

}
