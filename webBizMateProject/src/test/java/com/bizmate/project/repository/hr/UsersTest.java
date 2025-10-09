package com.bizmate.project.repository.hr;

import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.hr.Employees;
import com.bizmate.project.domain.hr.Users;
import com.bizmate.project.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@Slf4j
public class UsersTest {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EmployeesRepository employeesRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void InsertUsers() {

        List<Employees> employeesList = new ArrayList<>();

        employeesList = employeesRepository.findAll();

        System.out.println("employeesList = " + employeesList);
        System.out.println("0----------------------------------");
        employeesList.stream()
                .forEach(employees -> System.out.println("employees = " + employees.getEmpName()));


        Employees employees = employeesRepository.findById(2001)
                .orElseThrow(() -> new RuntimeException("없는 사원번호 입니다."));

        Employees employees1 = employeesRepository.findById(2002)
                .orElseThrow(() -> new RuntimeException("없는 사원 번호 입니다."));

        Employees employees2 = employeesRepository.findById(2003)
                .orElseThrow(() -> new RuntimeException("없는 사원 번호 입니다."));



        Users newUser = new Users();
        System.out.println("---------------------------------------------newUser.toString() = " + employees.getDepartments().getDeptId());


        System.out.println("-----------------------------------------employees = " + employees);



        newUser.setUserId(1010L);
        newUser.setEmployees(employees);
        newUser.setUsername(employees.getEmpName());
        newUser.setPasswordHash("12341234");
        newUser.setIsActiove("Y");
        newUser.setIsLocked("N");

        usersRepository.save(newUser);

        String projectNo = generateProjectNo(newUser);

        System.out.println("projectNo = " + projectNo);

        Users user2 = Users.builder()
                .employees(employees1)
                .username(employees1.getEmpName())
                .passwordHash("12341234")
                .isActiove("Y")
                .isLocked("N")
                .build();

        Users user3 = Users.builder()
                .employees(employees2)
                .username(employees2.getEmpName())
                .passwordHash("12341234")
                .isActiove("Y")
                .isLocked("N")
                .build();

        usersRepository.save(user2);
        usersRepository.save(user3);


    }

    @Transactional
   public String generateProjectNo(Users user){
        Long deptId = user.getEmployees().getDepartments().getDeptId();

        Long seq = projectRepository.getNextProjectSeq();

        return String.format("%d-%04d", deptId,seq);
    }
}
