package com.bizmate.groupware.repository;

import com.bizmate.groupware.domain.ApprovalDocuments;
import com.bizmate.groupware.dto.ApprovalDocumentsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalDocumentsRepository extends JpaRepository<ApprovalDocuments, String> {

}
