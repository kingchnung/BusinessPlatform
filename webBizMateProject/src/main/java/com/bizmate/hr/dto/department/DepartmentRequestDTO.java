package com.bizmate.hr.dto.department;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentRequestDTO {
    @NotBlank(message = "부서 이름은 필수 항목입니다.")
    private String deptName;

    @NotBlank(message = "부서 코드는 필수 항목입니다.")
    private String deptCode;
}