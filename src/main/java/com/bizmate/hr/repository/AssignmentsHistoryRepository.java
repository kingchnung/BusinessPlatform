package com.bizmate.hr.repository;

import com.bizmate.hr.domain.AssignmentsHistory;
import com.bizmate.hr.dto.assignment.AssignmentHistoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentsHistoryRepository extends JpaRepository<AssignmentsHistory, Long> {
    List<AssignmentsHistory> findByEmployee_EmpIdOrderByAssDateDesc(Long empId);
    List<AssignmentHistoryDTO> findByNewDepartment_DeptIdOrderByAssDateDesc(Long deptId);

}
