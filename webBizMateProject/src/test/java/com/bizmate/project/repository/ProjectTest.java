package com.bizmate.project.repository;


import com.bizmate.project.domain.Assign;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectMember;
import com.bizmate.project.domain.embeddables.ProjectMemberId;
import com.bizmate.project.domain.enums.AssignStatus;
import com.bizmate.project.domain.enums.ProjectImportance;
import com.bizmate.project.domain.enums.ProjectMemberStatus;
import com.bizmate.project.domain.enums.ProjectStatus;
import com.bizmate.project.domain.hr.Employees;
import com.bizmate.project.domain.hr.Users;
import com.bizmate.project.domain.sails.Client;
import com.bizmate.project.repository.hr.EmployeesRepository;
import com.bizmate.project.repository.hr.UsersRepository;
import com.bizmate.project.repository.salse.ClientRepository;
import com.bizmate.project.service.ProjectService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
public class ProjectTest {


    @Autowired
    private EmployeesRepository employeesRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private AssignRepository assignRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;


    @Test
    public void insertAssign(){
        Assign assign = Assign.builder()
                .taskName("test업무")
                .taskPriority(AssignStatus.BEFORE_START)
                .build();

        assignRepository.save(assign);
    }


    @Test
    public void readEmployees() {
        List<Employees> employeesList = new ArrayList<>();
        employeesList = employeesRepository.findAll();
        employeesList.stream()
                        .forEach(employees -> System.out.println("employees, = " + employees.getEmpName()));

        log.info("employeesList = " + employeesList);
        System.out.println("-----------------------------------------------------------------employeesList = " + employeesList);
    }

    @Test
    public void insertProject() {
        Client client  = clientRepository.findById("20240001A")
                .orElseThrow(() -> new RuntimeException("거래처를 찿을 수 없습니다"));

        Users user1 = usersRepository.findById(1010)
                .orElseThrow(() -> new RuntimeException("유저를 찿을 수 없습니다"));




        Project projectTest = Project.builder()
                .projectNo(projectService.getProjectNo(user1))
                .projectName("test2")
                .projectStartDate(LocalDateTime.now())
                .projectStatus(ProjectStatus.IN_PROGRESS)
                .projectImportance(ProjectImportance.LOW)
                .clientId(client)
                .userId(user1).build();

        projectRepository.save(projectTest);
        //project.setProjectId(projectService.getProjectNo(User user));
    }

    @Test
    public void projectMemberInsert(){
        Client client  = clientRepository.findById("20240001A")
                .orElseThrow(() -> new RuntimeException("거래처를 찿을 수 없습니다"));

        Users user1 = usersRepository.findById(1010)
                .orElseThrow(() -> new RuntimeException("유저를 찿을 수 없습니다"));

        String projectStatus = ProjectStatus.BEFORE_START.getStatus();

        Project projectTest = Project.builder()
                .projectNo(projectService.getProjectNo(user1))
                .projectName("test6")
                .projectStartDate(LocalDateTime.now())
                .projectStatus(ProjectStatus.IN_PROGRESS)
                .projectImportance(ProjectImportance.LOW)
                .clientId(client)
                .userId(user1)
                .managerName(user1.getUsername())
                .build();

        projectRepository.save(projectTest);

        Users user2 = usersRepository.findById(1010)
                .orElseThrow(() -> new RuntimeException("users를 찿을 수 없습니다"));

        ProjectMemberId projectMemberId = ProjectMemberId
                .builder()
                .projectId(projectTest.getProjectId())
                .userId(user2.getUserId())
                .build();

        Assign assign1 = assignRepository.findById(6)
                .orElseThrow(() -> new RuntimeException("업무를 찿을 수 없습니다."));

        ProjectMember projectMember = ProjectMember
                .builder()
                .id(projectMemberId)
                .projectId(projectTest)
                .userId(user2)
                .assign(assign1)
                .pmRoleName(ProjectMemberStatus.PRODUCT_OWNER)
                .build();

        projectMemberRepository.save(projectMember);


    }

}

