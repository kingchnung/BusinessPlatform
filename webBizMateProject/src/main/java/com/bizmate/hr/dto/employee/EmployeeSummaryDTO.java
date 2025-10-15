package com.bizmate.hr.dto.employee;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSummaryDTO {

    private Long EmpId;
    private String empName;
    private String deptName;
}
