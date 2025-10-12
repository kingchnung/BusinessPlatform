package com.bizmate.hr.dto.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * [EmployeeRequestDTO]
 * - 직원 정보 등록/수정 요청(Request)용 DTO
 */
@Data
public class EmployeeRequestDTO {

    @NotBlank(message = "사원 번호는 필수 항목입니다.")
    private String empNo;

    @NotBlank(message = "이름은 필수 항목입니다.")
    private String empName;

    @NotNull(message = "부서 ID는 필수 항목입니다.")
    private Long deptId; // ★ 부서(Dept) 엔티티가 아닌, ID(FK)로 받습니다.

    private long position;
    private String status;

    // 이외 등록/수정에 필요한 필드 추가
}