package com.bizmate.project.repository.hr;

import com.bizmate.project.domain.hr.Employees;
import com.bizmate.project.domain.hr.Users;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Slf4j
public class UsersTest {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EmployeesRepository employeesRepository;

    @Test
    public void InsertUsers() {
        Employees employees = employeesRepository.findById(2001)
                .orElseThrow(() -> new RuntimeException("없는 사원번호 입니다."));


        Users newUser = new Users();
        System.out.println("---------------------------------------------newUser.toString() = " + employees.getEmpName());


        System.out.println("-----------------------------------------employees = " + employees);

//        newUser1.builder().userId(1000).employees(employees)
//                .username(employees.getEmpName())
//                .build();
//       usersRepository.save(newUser1);

        newUser.setUserId(1010);
        newUser.setEmployees(employees);
        newUser.setUsername(employees.getEmpName());
        newUser.setPasswordHash("12341234");
        newUser.setIsActiove("Y");
        newUser.setIsLocked("N");

        usersRepository.save(newUser);


    }
}
