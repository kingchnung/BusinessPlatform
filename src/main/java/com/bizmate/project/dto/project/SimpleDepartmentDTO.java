package com.bizmate.project.dto.project;

import com.bizmate.hr.domain.Department;
import lombok.Getter;

@Getter
public class SimpleDepartmentDTO {
    private final Long deptId;
    private final String deptName;
    private final String deptCode;

    public SimpleDepartmentDTO(Department department) {
        this.deptId = department.getDeptId();
        this.deptName = department.getDeptName();
        this.deptCode = department.getDeptCode();
    }
}
