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
public class EmployeeServiceImpl implements EmployeeService { // 구현체명 일관성 유지를 위해 수정

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;// FK 처리를 위해 필요
    private final PositionRepository positionRepository;
    private final GradeRepository gradeRepository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true) // 읽기 전용으로 설정
    public List<EmployeeDTO> getAllEmployees() {
        // ★ 변경: findAllWithDepartment() (Fetch Join 메서드) 사용
        return employeeRepository.findAllWithDepartment().stream()
                .map(EmployeeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployee(Long empId) {
        // ★ 변경: findByIdWithDepartment() (Fetch Join 메서드) 사용
        Employee employee = employeeRepository.findByIdWithDepartment(empId)
                .orElseThrow(() -> new EntityNotFoundException("사원 ID " + empId + "를 찾을 수 없습니다."));

        return EmployeeDTO.fromEntity(employee);
    }

    @Override
    public EmployeeDTO saveEmployee(Long empId, EmployeeRequestDTO requestDTO) {
        // 1. 엔티티 조회 (수정 시) 또는 신규 생성 (등록 시)
        Employee employee;
        if (empId != null) {
            employee = employeeRepository.findById(empId)
                    .orElseThrow(() -> new EntityNotFoundException("사원 ID " + empId + "를 찾을 수 없습니다."));
        } else {
            employee = new Employee();
        }

        // 2. 부서 엔티티 조회 (RequestDTO에서 받은 deptId 사용)
        Department department = departmentRepository.findById(requestDTO.getDeptId())
                .orElseThrow(() -> new EntityNotFoundException("부서 ID " + requestDTO.getDeptId() + "를 찾을 수 없습니다."));

        Position position = positionRepository.findById(requestDTO.getPositionCode()) // ★ requestDTO의 positionId 필드를 사용해야 함
                .orElseThrow(() -> new EntityNotFoundException("직위 ID " + requestDTO.getPositionCode() + "를 찾을 수 없습니다."));

        Grade grade = gradeRepository.findById(requestDTO.getGradeCode()) //
                .orElseThrow(() -> new EntityNotFoundException("직위 ID " + requestDTO.getGradeCode() + "를 찾을 수 없습니다."));
        // 3. 엔티티에 DTO 값 반영
        employee.setEmpNo(requestDTO.getEmpNo());
        employee.setEmpName(requestDTO.getEmpName());
        employee.setPhone(requestDTO.getPhone());
        employee.setEmail(requestDTO.getEmail());
        employee.setStartDate(requestDTO.getStartDate());

        employee.setDepartment(department); // ★ FK 설정 (엔티티 필드명에 맞게 setDepartment() 호출)
        employee.setPosition(position);
        employee.setGrade(grade);
        employee.setStatus(requestDTO.getStatus());
        // ... (나머지 필드)

        // 4. 저장 및 DTO로 변환하여 반환
        Employee savedEmployee = employeeRepository.save(employee);

        if(empId == null){
            // 초기 비밀번호는 사원 번호(empNo)와 동일하게 설정 (운영 정책에 따라 변경 가능)
            String initialPassword = savedEmployee.getEmpNo();
            userService.createUserAccount(savedEmployee, initialPassword);
        }
        return EmployeeDTO.fromEntity(savedEmployee);
    }

    @Override
    public void deleteEmployee(Long empId) {
        // 삭제 전 존재 여부 확인 로직을 추가할 수도 있습니다.
        employeeRepository.deleteById(empId);
    }
}