package com.bizmate.groupware.approval.repository;

import com.bizmate.groupware.approval.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("SELECT d.departmentCode FROM Department d WHERE d.id = :departmentId")
    Optional<String> findDepartmentCodeById(@Param("departmentId") Long departmentId);
}
