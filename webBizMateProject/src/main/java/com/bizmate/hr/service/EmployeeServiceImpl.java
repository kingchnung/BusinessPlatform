package com.bizmate.hr.service;

import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.code.Grade;
import com.bizmate.hr.domain.code.Position;
import com.bizmate.hr.dto.employee.EmployeeDTO;
import com.bizmate.hr.dto.employee.EmployeeRequestDTO;
import com.bizmate.hr.repository.DepartmentRepository;
import com.bizmate.hr.repository.EmployeeRepository;

import com.bizmate.hr.repository.GradeRepository;
import com.bizmate.hr.repository.PositionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final GradeRepository gradeRepository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAllWithDepartmentAndPosition().stream()
                .map(EmployeeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployee(Long empId) {
        Employee employee = employeeRepository.findByIdWithDepartmentAndPosition(empId)
                .orElseThrow(() -> new EntityNotFoundException("사원 ID " + empId + "를 찾을 수 없습니다."));
        return EmployeeDTO.fromEntity(employee);
    }

    @Override
    public EmployeeDTO saveEmployee(Long empId, EmployeeRequestDTO requestDTO) {
        Employee employee;

        if (empId != null) {
            // 수정 모드
            employee = employeeRepository.findById(empId)
                    .orElseThrow(() -> new EntityNotFoundException("사원 ID " + empId + "를 찾을 수 없습니다."));
        } else {
            // 신규 등록
            employee = new Employee();
            // 🔹 자동 사번 생성 (DTO에서 받지 않음)
            String empNo = generateEmpNo(requestDTO.getDeptId());
            employee.setEmpNo(empNo);
        }

        // 🔹 FK 엔티티 조회
        Department department = departmentRepository.findById(requestDTO.getDeptId())
                .orElseThrow(() -> new EntityNotFoundException("부서 ID " + requestDTO.getDeptId() + "를 찾을 수 없습니다."));
        Position position = positionRepository.findById(requestDTO.getPositionCode())
                .orElseThrow(() -> new EntityNotFoundException("직책 코드 " + requestDTO.getPositionCode() + "를 찾을 수 없습니다."));
        Grade grade = gradeRepository.findById(requestDTO.getGradeCode())
                .orElseThrow(() -> new EntityNotFoundException("직급 코드 " + requestDTO.getGradeCode() + "를 찾을 수 없습니다."));

        // 🔹 기본 필드 매핑
        employee.setEmpName(requestDTO.getEmpName());
        employee.setPhone(requestDTO.getPhone());
        employee.setEmail(requestDTO.getEmail());
        employee.setStartDate(requestDTO.getStartDate());
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setGrade(grade);
        employee.setStatus(requestDTO.getStatus() != null
                ? requestDTO.getStatus().trim().toUpperCase()
                : "ACTIVE");

        // 🔹 저장
        Employee savedEmployee = employeeRepository.save(employee);

        // 🔹 신규 직원일 경우 자동 계정 생성
        if (empId == null) {
            userService.createUserAccount(savedEmployee);
        }

        return EmployeeDTO.fromEntity(savedEmployee);
    }

    @Override
    public void deleteEmployee(Long empId) {
        employeeRepository.deleteById(empId);
    }

    // ===============================
    // 🔹 사번 자동 생성 로직
    // ===============================
    private String generateEmpNo(Long deptId) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new EntityNotFoundException("부서 ID " + deptId + "를 찾을 수 없습니다."));

        String companyCode = "50"; // 고정
        String deptCode = dept.getDeptCode(); // 예: "31"
        long count = employeeRepository.countByDepartment_DeptCode(deptCode);
        String sequence = String.format("%03d", count + 1);

        return companyCode + deptCode + sequence; // 예: 5031001
    }
}
