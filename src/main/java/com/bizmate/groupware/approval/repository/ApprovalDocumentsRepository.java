package com.bizmate.groupware.approval.repository;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.DocumentStatus;
import com.bizmate.hr.domain.Departments;
import com.bizmate.hr.domain.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApprovalDocumentsRepository extends JpaRepository<ApprovalDocuments, String> {

    /* ----------------------------- 기본 조회 ------------------------------ */
    List<ApprovalDocuments> findByAuthorUser(Users author);

    List<ApprovalDocuments> findByDepartment(Departments department);

    List<ApprovalDocuments> findByStatus(DocumentStatus status);

    /* ----------------------------- 사용자별 문서 ------------------------------ */
    List<ApprovalDocuments> findByAuthorUser_UserId(Long userId);

    /* ----------------------------- 페이징 검색 ------------------------------ */
    Page<ApprovalDocuments> findAll(Pageable pageable);


    long countByDepartment_DeptIdAndCreatedAtBetween(Long departmentId,
                                                     LocalDateTime start,
                                                     LocalDateTime end);
}
