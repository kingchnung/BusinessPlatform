package com.bizmate.hr.service;

import com.bizmate.hr.dto.employee.EmployeeDTO;
import com.bizmate.hr.dto.employee.EmployeeDetailDTO;
import com.bizmate.hr.dto.employee.EmployeeRequestDTO;
import java.util.List;

public interface EmployeeService { // ★ 인터페이스 이름 변경

    EmployeeDTO saveEmployee(Long empId, EmployeeRequestDTO requestDTO);
    List<EmployeeDTO> getAllEmployees();
    EmployeeDTO getEmployee(Long empId);
    void deleteEmployee(Long empId);
    EmployeeDetailDTO getEmployeeDetail(Long empId);
}