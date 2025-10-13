package com.bizmate.hr.repository;

import com.bizmate.hr.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    // 부서명으로 조회하는 메서드를 추가할 수 있습니다.
    Optional<Department> findByDeptName(String deptName);

    Optional<Department> findByDeptCode(String code);

}