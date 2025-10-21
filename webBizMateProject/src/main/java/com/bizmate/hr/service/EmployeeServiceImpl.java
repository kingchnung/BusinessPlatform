package com.bizmate.hr.service;

import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.domain.code.Grade;
import com.bizmate.hr.domain.code.Position;
import com.bizmate.hr.dto.employee.*;
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
    public EmployeeDTO createEmployee(EmployeeCreateRequestDTO dto) {
        // ✅ 1. 부서 코드로 다음 사번 생성
        Department dept = departmentRepository.findByDeptCode(dto.getDeptCode())
                .orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다."));

        String empNo = generateEmpNo(dto.getDeptCode());  // 자동 생성

        // 🔹 FK 엔티티 조회
        Department department = departmentRepository.findByDeptCode(dto.getDeptCode())
                .orElseThrow(() -> new EntityNotFoundException("부서 ID " + dto.getDeptCode() + "를 찾을 수 없습니다."));
        Position position = positionRepository.findById(dto.getPositionCode())
                .orElseThrow(() -> new EntityNotFoundException("직책 코드 " + dto.getPositionCode() + "를 찾을 수 없습니다."));
        Grade grade = gradeRepository.findById(dto.getGradeCode())
                .orElseThrow(() -> new EntityNotFoundException("직급 코드 " + dto.getGradeCode() + "를 찾을 수 없습니다."));

        // 🔹 기본 필드 매핑
        Employee employee = new Employee();
        employee.setEmpNo(empNo);
        employee.setEmpName(dto.getEmpName());
        employee.setGender(dto.getGender());
        employee.setBirthDate(dto.getBirthDate());
        employee.setPhone(dto.getPhone());
        employee.setEmail(dto.getEmail());
        employee.setAddress(dto.getAddress());
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setGrade(grade);
        employee.setStartDate(dto.getStartDate());
        employee.setStatus("ACTIVE");

        // 🔹 저장
        Employee savedEmployee = employeeRepository.save(employee);

        // 🔹 신규 직원일 경우 자동 계정 생성

        userService.createUserAccount(savedEmployee);

        return EmployeeDTO.fromEntity(savedEmployee);
    }

    @Override
    @Transactional
    public EmployeeDTO updateMyInfo(Long empId, EmployeeUpdateRequestDTO requestDTO) {
        // 1. 기존 직원 조회 (getEmployee 메서드에서 이미 이 로직을 사용하고 있음)
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new EntityNotFoundException("사원 ID " + empId + "를 찾을 수 없습니다."));

        // 2. 본인 수정 가능 필드만 반영
        // 💡 null 체크를 통해 전송된 값만 반영되도록 할 수 있지만, DTO에서 @NotNull을 사용했다면 생략 가능합니다.
        employee.setPhone(requestDTO.getPhone());
        employee.setEmail(requestDTO.getEmail());
        employee.setAddress(requestDTO.getAddress());

        // 3. 관리자 전용 필드 (부서, 직급, 직책, 상태 등)는 아예 건드리지 않음
        // **[핵심] FK 변경 로직 및 관리자 항목 로직 모두 제거**

        // 4. 저장
        Employee updated = employeeRepository.save(employee);

        syncUserInfo(updated);

        return EmployeeDTO.fromEntity(updated);
    }

    @Override
    public EmployeeDTO updateEmployee(Long empId, EmployeeUpdateRequestDTO requestDTO) {
        // 🔹 기존 직원 조회
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new EntityNotFoundException("사원 ID " + empId + "를 찾을 수 없습니다."));

        // 🔹 수정 가능한 필드만 반영
        employee.setPhone(requestDTO.getPhone());
        employee.setEmail(requestDTO.getEmail());
        employee.setAddress(requestDTO.getAddress());

        // ✅ 🔹 상태값(status) 갱신 추가
        if (requestDTO.getStatus() != null) {
            employee.setStatus(requestDTO.getStatus());
        }

        // 🔹 FK 변경(선택적)
        if (requestDTO.getDeptCode() != null) {
            Department dept = departmentRepository.findByDeptCode(requestDTO.getDeptCode())
                    .orElseThrow(() -> new EntityNotFoundException("부서 코드 " + requestDTO.getDeptCode() + "를 찾을 수 없습니다."));
            employee.setDepartment(dept);
        }

        if (requestDTO.getPositionCode() != null) {
            Position pos = positionRepository.findById(requestDTO.getPositionCode())
                    .orElseThrow(() -> new EntityNotFoundException("직책 코드 " + requestDTO.getPositionCode() + "를 찾을 수 없습니다."));
            employee.setPosition(pos);
        }

        if (requestDTO.getGradeCode() != null) {
            Grade grade = gradeRepository.findById(requestDTO.getGradeCode())
                    .orElseThrow(() -> new EntityNotFoundException("직급 코드 " + requestDTO.getGradeCode() + "를 찾을 수 없습니다."));
            employee.setGrade(grade);
        }

        // 🔹 저장
        Employee updatedEmployee = employeeRepository.save(employee);

        // 🔹 User 정보 동기화
        syncUserInfo(updatedEmployee);

        return EmployeeDTO.fromEntity(updatedEmployee);
    }

    @Override
    public List<EmployeeStatisticDTO> getAgeStatistics() {
        List<Object[]> result = employeeRepository.getAgeStatistics();
        return result.stream()
                .map(r -> new EmployeeStatisticDTO((String) r[0], ((Number) r[1]).longValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeStatisticDTO> getGradeStatistics() {
        List<Object[]> result = employeeRepository.getGradeStatistics();
        return result.stream()
                .map(r -> new EmployeeStatisticDTO((String) r[0], ((Number) r[1]).longValue()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getEmployeesByDepartment(Long deptId) {
        return employeeRepository.findByDepartment_DeptId(deptId).stream()
                .map(EmployeeDTO::fromEntity)
                .collect(Collectors.toList());
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
        userRepository.findByEmployee(employee)
                .ifPresent(user -> {
                    user.setEmpName(employee.getEmpName());
                    user.setEmail(employee.getEmail());
                    user.setPhone(employee.getPhone());
                    if (employee.getDepartment() != null) {
                        user.setDeptName(employee.getDepartment().getDeptName());
                        user.setDeptCode(employee.getDepartment().getDeptCode());
                    }
                    if (employee.getPosition() != null) {
                        user.setPositionName(employee.getPosition().getPositionName());
                    }
                    userRepository.saveAndFlush(user);
                });
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
