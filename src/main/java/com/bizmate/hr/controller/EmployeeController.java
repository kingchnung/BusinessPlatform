package com.bizmate.hr.controller;

import com.bizmate.hr.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeRepository employeeRepository;

    @GetMapping
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(e -> new EmployeeResponse(
                        Math.toIntExact(e.getEmpId()),
                        e.getEmpName(),
                        e.getDepartments().getDeptName(),
                        e.getPositionsCode().getPositionName(),
                        e.getEmail()
                ))
                .toList();
    }

    public record EmployeeResponse(
            Integer empId,
            String empName,
            String deptName,
            String positionName,
            String email
    ) {}

}
