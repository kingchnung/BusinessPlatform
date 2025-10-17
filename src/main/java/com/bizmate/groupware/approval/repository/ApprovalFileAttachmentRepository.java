package com.bizmate.groupware.approval.repository;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.ApprovalFileAttachment;
import com.bizmate.groupware.approval.dto.ApprovalFileAttachmentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ApprovalFileAttachmentRepository extends JpaRepository<ApprovalFileAttachment, Long> {

    /**
     * ✅ 임시 업로드된(PENDING) 파일을 문서와 연결
     */
    @Modifying
    @Transactional
    @Query(value = """
                UPDATE APPROVAL_FILE_ATTACHMENT 
                SET DOC_ID = :#{#document.docId},
                    STATUS = 'LINKED'
                WHERE STATUS = 'PENDING'
                  AND CREATED_BY = :username
            """, nativeQuery = true)
    int linkPendingFiles(@Param("document") ApprovalDocuments document,
                         @Param("username") String username);

    List<ApprovalFileAttachment> findByDocument_DocId(String docId);
}
