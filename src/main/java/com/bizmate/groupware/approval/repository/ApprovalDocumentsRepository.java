package com.bizmate.groupware.approval.repository;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalDocumentsRepository extends JpaRepository<ApprovalDocuments, String>,
        JpaSpecificationExecutor<ApprovalDocuments> {

    List<ApprovalDocuments> findByStatusNot(DocumentStatus status);
}
