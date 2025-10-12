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

    // ★ 추가: Fetch Join을 사용하여 Employee와 Department를 한 번에 조회 (N+1 문제 해결)
    @Query("SELECT e FROM Employee e JOIN FETCH e.department") // e.department는 Employee 엔티티의 필드명
    List<Employee> findAllWithDepartment();

    // ★ 추가: 특정 직원 조회 시에도 Fetch Join 적용
    @Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.empId = :empId")
    Optional<Employee> findByIdWithDepartment(@Param("empId") Long empId);
}
