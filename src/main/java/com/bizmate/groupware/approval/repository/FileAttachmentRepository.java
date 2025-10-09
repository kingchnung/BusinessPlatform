package com.bizmate.groupware.approval.repository;

import com.bizmate.groupware.approval.domain.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {
    List<FileAttachment> findByDocument_DocId(String documentId);
    List<FileAttachment> findByDocumentIsNull();
}
