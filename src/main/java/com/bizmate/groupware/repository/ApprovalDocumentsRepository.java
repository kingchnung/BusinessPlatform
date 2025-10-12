package com.bizmate.groupware.repository;

import com.bizmate.groupware.domain.ApprovalDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalDocumentsRepository extends JpaRepository<ApprovalDocuments, String> {

}
