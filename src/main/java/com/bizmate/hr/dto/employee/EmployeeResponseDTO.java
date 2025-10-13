package com.bizmate.hr.dto.employee;

import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.code.Grade;
import com.bizmate.hr.domain.code.Position;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * [직원 정보 조회 및 응답 DTO]
 * - 프론트엔드로 데이터를 전송할 때 사용
 * - 연관된 엔티티(Department, User)의 이름 정보 포함
 */
@Getter
@Builder
public class EmployeeResponseDTO {

    private final Long empId;
    private final String empNo;
    private final String empName;
    private final String gender;
    private final LocalDate birthDate;
    private final String phone;
    private final String email;
    private final String address;

    // 연관 엔티티 정보 (부서)
    private final Long deptId;
    private final String deptName; // Department Entity에서 가져옴

    private final Position position; // 직위
    private final Grade grade;    // 직급
    private final LocalDate startDate;
    private final LocalDate leaveDate;
    private final String status;
    private final Double careerYears;
    private final String ssnMask;

    // 생성/수정자 정보 (User Entity에서 가져옴)
    private final String createdBy; // 생성자 이름
    private final LocalDateTime creDate;
    private final String updatedBy; // 수정자 이름
    private final LocalDateTime updDate;

    /**
     * Entity -> DTO 변환 생성자/메서드 (편의를 위해 Builder 패턴 사용)
     */
    public static EmployeeResponseDTO fromEntity(Employee employee) {
        // null 체크를 통해 NullPointerException 방지
        String deptName = employee.getDepartment() != null ? employee.getDepartment().getDeptName() : "미지정";
        String createdBy = employee.getCreatedBy() != null ? employee.getCreatedBy().getEmpName() : "시스템";
        String updatedBy = employee.getUpdatedBy() != null ? employee.getUpdatedBy().getEmpName() : "시스템";

        return EmployeeResponseDTO.builder()
                .empId(employee.getEmpId())
                .empNo(employee.getEmpNo())
                .empName(employee.getEmpName())
                .gender(employee.getGender())
                .birthDate(employee.getBirthDate())
                .phone(employee.getPhone())
                .email(employee.getEmail())
                .address(employee.getAddress())
                .deptId(employee.getDepartment() != null ? employee.getDepartment().getDeptId() : null)
                .deptName(deptName) // 부서명 포함
                .position(employee.getPosition())
                .grade(employee.getGrade())
                .startDate(employee.getStartDate())
                .leaveDate(employee.getLeaveDate())
                .status(employee.getStatus())
                .careerYears(employee.getCareerYears())
                .ssnMask(employee.getSsnMask())
                .createdBy(createdBy)
                .creDate(employee.getCreDate())
                .updatedBy(updatedBy)
                .updDate(employee.getUpdDate())
                .build();
    }
}