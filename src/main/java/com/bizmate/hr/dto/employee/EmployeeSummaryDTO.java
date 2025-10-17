package com.bizmate.hr.dto.employee;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSummaryDTO {

    private Long id;
    private String empName;
    private String deptName;
}
