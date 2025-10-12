package com.bizmate.groupware.approval.repository;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.FileAttachment;
import com.bizmate.hr.domain.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {
    List<FileAttachment> findByDocument_DocId(String docId);

    /**
     * 임시 업로드 파일을 상신 시 문서에 연결
     */
    @Modifying
    @Query("UPDATE FileAttachment f SET f.document = :document WHERE f.document IS NULL AND f.uploader = :uploader")
    int linkPendingFiles(@Param("document") ApprovalDocuments document, @Param("uploader") UserEntity uploader);
}
