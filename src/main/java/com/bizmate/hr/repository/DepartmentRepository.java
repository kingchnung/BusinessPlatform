package com.bizmate.hr.repository;

import com.bizmate.hr.domain.Departments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Departments, Integer> {

    @Query("SELECT d.deptCode FROM Departments d WHERE d.deptId = :deptId")
    Optional<String> findDeptCodeById(@Param("departmentId") Integer departmentId);
}
