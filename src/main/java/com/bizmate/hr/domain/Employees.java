package com.bizmate.hr.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "employees")
@NoArgsConstructor
@AllArgsConstructor
public class Employees {

    @Id
    @Column(name = "emp_id")
    private Long empId;

    @Column
    private String empName;

    @Column
    private LocalDateTime birthDate;

    @Column
    private String email;

    @Column
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    private Departments departments;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_code")
    private Grade gradeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_code")
    private Positions positionsCode;
}
