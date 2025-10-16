package com.bizmate.project.repository;


import com.bizmate.project.domain.hr.Employees;
import com.bizmate.project.repository.hr.EmployeesRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
public class ProjectTest {


    @Autowired
    private EmployeesRepository employeesRepository;


    @Test
    public void readEmployees() {
        List<Employees> employeesList = new ArrayList<>();
        employeesList = employeesRepository.findAll();
        employeesList.stream()
                        .forEach(employees -> System.out.println("employees, = " + employees.getEmpName()));

        log.info("employeesList = " + employeesList);
        System.out.println("-----------------------------------------------------------------employeesList = " + employeesList);
    }

}

