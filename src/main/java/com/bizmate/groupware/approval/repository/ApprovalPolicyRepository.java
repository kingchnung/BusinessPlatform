package com.bizmate.groupware.approval.repository;

import com.bizmate.groupware.approval.domain.ApprovalPolicy;
import com.bizmate.groupware.approval.domain.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApprovalPolicyRepository extends JpaRepository<ApprovalPolicy, Long> {
    Optional<ApprovalPolicy> findByDocType(String docType);

    Optional<ApprovalPolicy> findByDocTypeAndIsActiveTrue(DocumentType docType);
}
