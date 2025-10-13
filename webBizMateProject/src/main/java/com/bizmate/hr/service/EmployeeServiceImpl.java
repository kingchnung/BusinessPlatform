package com.bizmate.hr.service;

import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.domain.code.Grade;
import com.bizmate.hr.domain.code.Position;
import com.bizmate.hr.dto.employee.EmployeeDTO;
import com.bizmate.hr.dto.employee.EmployeeDetailDTO;
import com.bizmate.hr.dto.employee.EmployeeRequestDTO;
import com.bizmate.hr.repository.*;

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
    private final UserRepository userRepository;

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
            String empNo = generateEmpNo(requestDTO.getDeptCode());
            employee.setEmpNo(empNo);


        }


        // 🔹 FK 엔티티 조회
        Department department = departmentRepository.findByDeptCode(requestDTO.getDeptCode())
                .orElseThrow(() -> new EntityNotFoundException("부서 ID " + requestDTO.getDeptCode() + "를 찾을 수 없습니다."));
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
        } else {
            //수정시 uesr정보 동기화
            syncUserInfo(savedEmployee);
        }

        return EmployeeDTO.fromEntity(savedEmployee);
    }

    @Override
    public void deleteEmployee(Long empId) {
        employeeRepository.deleteById(empId);
    }

    @Override
    public EmployeeDetailDTO getEmployeeDetail(Long empId) {
        Employee employee = employeeRepository.findEmployeeDetailById(empId)
                .orElseThrow(()-> new EntityNotFoundException("직원을 찾을 수 없습니다"));
        return EmployeeDetailDTO.fromEntity(employee);
    }




    /**
     * 🔹 직원 정보 변경 시 UserEntity의 복제 필드를 동기화하는 메서드
     */
    public void syncUserInfo(Employee employee) {
        UserEntity user = userRepository.findByEmployee(employee)
                .orElseThrow(() -> new EntityNotFoundException("연결된 사용자 계정을 찾을 수 없습니다."));

        user.setEmpName(employee.getEmpName());
        user.setEmail(employee.getEmail());
        user.setPhone(employee.getPhone());
        user.setDeptName(employee.getDepartment().getDeptName());
        user.setPositionName(employee.getPosition().getPositionName());
        user.setDeptCode(employee.getDepartment().getDeptCode());
        userRepository.save(user);
    }



    // ===============================
    // 🔹 사번 자동 생성 로직
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public String generateEmpNo(String deptCode) {
        Department dept = departmentRepository.findByDeptCode(deptCode)
                .orElseThrow(() -> new EntityNotFoundException("부서 ID " + deptCode + "를 찾을 수 없습니다."));

        String companyCode = "50"; // 고정
        String Code = dept.getDeptCode(); // 예: "31"
        long count = employeeRepository.countByDepartment_DeptCode(Code);
        String sequence = String.format("%03d", count + 1);

        return companyCode + Code + sequence; // 예: 5031001
    }
}
