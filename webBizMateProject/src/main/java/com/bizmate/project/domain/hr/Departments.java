package com.bizmate.project.domain.hr;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "departments")
public class Departments {

    @Id
    @Column(name = "dept_id")
    private Long deptId;

    @Column
    private String deptCode;

    @Column
    private String deptName;


}
