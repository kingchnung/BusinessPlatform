package com.bizmate.hr.dto.employee;

import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.code.Position;
import lombok.Builder;
import lombok.Data;

/**
 * [EmployeeDTO]
 * - 직원 정보 조회 응답(Response)용 DTO
 */
@Data
@Builder
public class EmployeeDTO {
    private Long empId;
    private String empNo;
    private String empName;
    private String deptName; // 부서명 (Dept 엔티티에서 추출)
    private Long positionCode;    // 직급 ID
    private String positionName;
    private String status; // 재직, 퇴사 등

    /** Employee Entity를 DTO로 변환하는 팩토리 메서드 */
    public static EmployeeDTO fromEntity(Employee employee) {
        Position position = employee.getPosition();

        return EmployeeDTO.builder()
                .empId(employee.getEmpId())
                .empNo(employee.getEmpNo())
                .empName(employee.getEmpName())
                .deptName(employee.getDepartment() != null ? employee.getDepartment().getDeptName() : null)
                .positionCode(position != null ? position.getPositionCode() : null)
                .positionName(position != null ? position.getPositionName() : null)
                .status(employee.getStatus())
                .build();
    }
}