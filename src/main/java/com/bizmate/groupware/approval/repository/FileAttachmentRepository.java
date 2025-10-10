package com.bizmate.groupware.approval.repository;

import com.bizmate.groupware.approval.domain.FileAttachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {
    Page<FileAttachment> findByDocument_DocId(String docId, Pageable pageable);
    List<FileAttachment> findByDocumentIsNull();
}
