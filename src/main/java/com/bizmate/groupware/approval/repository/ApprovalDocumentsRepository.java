package com.bizmate.groupware.approval.repository;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.DocumentStatus;
import com.bizmate.groupware.approval.dto.ApprovalDocumentsDto;
import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.user.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 전자결재 문서 Repository
 * - Spring Data JPA 기반
 * - 기본 CRUD + 조건검색
 */
@Repository
public interface ApprovalDocumentsRepository extends JpaRepository<ApprovalDocuments, String> {

    /** 특정 부서 내 문서 목록 */
    List<ApprovalDocuments> findByDepartment(Department department);

    /** 작성자별 문서 목록 */
    List<ApprovalDocuments> findByAuthorUser(UserEntity authorUser);

    /** 상태별 문서 목록 */
    List<ApprovalDocuments> findByStatus(DocumentStatus status);

    /** 작성자 userId로 조회 (JWT 기반) */
    List<ApprovalDocuments> findByAuthorUser_UserId(Long userId);

    /** 날짜 범위 내 부서별 카운트 (문서번호 생성용) */
    long countByDepartment_DeptIdAndCreatedAtBetween(Long deptId,
                                                     LocalDateTime start,
                                                     LocalDateTime end);

    /** 페이징 검색 */
    Page<ApprovalDocuments> findAll(Pageable pageable);

    @Query("SELECT d FROM ApprovalDocuments d WHERE d.status <> 'DELETED'")
    List<ApprovalDocuments> findAllActive();

}
