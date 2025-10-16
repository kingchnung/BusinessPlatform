package com.bizmate.project.repository.hr;

import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.repository.EmployeeRepository;
import com.bizmate.hr.repository.UserRepository;
import com.bizmate.project.domain.Project;
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
    private UserRepository usersRepository;

    @Autowired
    private EmployeeRepository employeesRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void InsertUsers() {

        List<Employee> employeesList = new ArrayList<>();

        employeesList = employeesRepository.findAll();

        System.out.println("employeesList = " + employeesList);
        System.out.println("0----------------------------------");
        employeesList.stream()
                .forEach(employees -> System.out.println("employees = " + employees.getEmpName()));


        Employee employees = employeesRepository.findById(2001L)
                .orElseThrow(() -> new RuntimeException("없는 사원번호 입니다."));

        Employee employees1 = employeesRepository.findById(2002L)
                .orElseThrow(() -> new RuntimeException("없는 사원 번호 입니다."));

        Employee employees2 = employeesRepository.findById(2003L)
                .orElseThrow(() -> new RuntimeException("없는 사원 번호 입니다."));



        UserEntity newUser = new UserEntity();
        System.out.println("---------------------------------------------newUser.toString() = " + employees.getDepartment().getDeptId());


        System.out.println("-----------------------------------------employees = " + employees);



        newUser.setUserId(1010L);
        newUser.setEmployee(employees);
        newUser.setUsername(employees.getEmpName());
        newUser.setPwHash("12341234");
        newUser.setIsActive("Y");
        newUser.setIsLocked("N");

        usersRepository.save(newUser);

        String projectNo = generateProjectNo(newUser);

        System.out.println("projectNo = " + projectNo);

        UserEntity user2 = UserEntity.builder()
                .employee(employees1)
                .username(employees1.getEmpName())
                .pwHash("12341234")
                .isActive("Y")
                .isLocked("N")
                .build();

        UserEntity user3 = UserEntity.builder()
                .employee(employees2)
                .username(employees2.getEmpName())
                .pwHash("12341234")
                .isActive("Y")
                .isLocked("N")
                .build();

        usersRepository.save(user2);
        usersRepository.save(user3);


    }

    @Transactional
   public String generateProjectNo(UserEntity user){
        Long deptId = user.getEmployee().getDepartment().getDeptId();

        Long seq = projectRepository.getNextProjectSeq();

        return String.format("%d-%04d", deptId,seq);
    }
}
