package com.bizmate.hr.service;
import com.bizmate.hr.dto.assignment.AssignmentHistoryDTO;
import com.bizmate.hr.dto.assignment.AssignmentHistoryRequestDTO;

import java.util.List;

public interface AssignmentsHistoryService {
    List<AssignmentHistoryDTO> getAllHistories();
    List<AssignmentHistoryDTO> getHistoriesByEmployee(Long empId);
    AssignmentHistoryDTO createAssignment(AssignmentHistoryRequestDTO requestDTO, String createdByUsername);
}