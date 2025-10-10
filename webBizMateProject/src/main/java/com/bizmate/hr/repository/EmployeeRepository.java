package com.bizmate.hr.repository;

import com.bizmate.hr.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmpNo(String empNo);
    List<Employee> findByEmpNameContaining(String name);
    List<Employee> findByStatus(String status);

    // ★★★ 수정: Position도 함께 Fetch Join하여 가져옵니다. ★★★
    // Position과 Department 모두 Eager Loading합니다.
    @Query("SELECT e FROM Employee e JOIN FETCH e.department JOIN FETCH e.position")
    List<Employee> findAllWithDepartmentAndPosition(); // 메서드명도 변경 (명확성을 위해)

    // ★ 수정: 특정 직원 조회 시에도 Position Fetch Join 적용
    @Query("SELECT e FROM Employee e JOIN FETCH e.department JOIN FETCH e.position WHERE e.empId = :empId")
    Optional<Employee> findByIdWithDepartmentAndPosition(@Param("empId") Long empId);

    long countByDepartment_DeptCode(String deptCode);
}
