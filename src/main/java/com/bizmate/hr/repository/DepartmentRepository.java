package com.bizmate.hr.repository;

import com.bizmate.hr.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findAllByOrderByDeptCodeAsc();
    Optional<Department> findByDeptCode(String deptCode);
    List<Department> findByParentDept_DeptId(Long parentDeptId);
}
