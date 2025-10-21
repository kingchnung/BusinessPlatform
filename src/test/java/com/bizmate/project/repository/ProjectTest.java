//package com.bizmate.project.repository;
//
//
//import com.bizmate.hr.domain.UserEntity;
//import com.bizmate.hr.repository.EmployeeRepository;
//import com.bizmate.hr.repository.UserRepository;
//import com.bizmate.project.domain.ProjectTask;
//import com.bizmate.project.domain.Project;
//import com.bizmate.project.domain.enums.task.TaskStatus;
//import com.bizmate.project.domain.enums.project.ProjectImportance;
//import com.bizmate.project.domain.enums.project.ProjectStatus;
//import com.bizmate.project.service.ProjectService;
//import com.bizmate.salesPages.client.domain.Client;
//import com.bizmate.salesPages.client.repository.ClientRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//
//import java.time.LocalDateTime;
//
//@SpringBootTest
//@Slf4j
//@TestConfiguration
//public class ProjectTest {
//
//
//    @Autowired
//    private EmployeeRepository employeesRepository;
//
//    @Autowired
//    private ProjectRepository projectRepository;
//
//    @Autowired
//    private ClientRepository clientRepository;
//
//    @Autowired
//    private UserRepository usersRepository;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private ProjectTaskRepository projectTaskRepository;
//
//    @Autowired
//    private ProjectMemberRepository projectMemberRepository;
//
//    @Test
//    public void test1(){
//        int i = 1+2;
//        System.out.println("i = " + i);
//    }
//
//
//    @Test
//    public void insertAssign(){
//        ProjectTask projectTask = ProjectTask.builder()
//                .taskName("test업무")
//                .status(TaskStatus.BEFORE_START)
//                .build();
//
//        projectTaskRepository.save(projectTask);
//    }
//
//
//
//    @Test
//    public void insertProject() {
//        Client client  = clientRepository.findById(21L)
//                .orElseThrow(() -> new RuntimeException("거래처를 찿을 수 없습니다"));
//
//        UserEntity user1 = usersRepository.findById(5L)
//                .orElseThrow(() -> new RuntimeException("유저를 찿을 수 없습니다"));
//
//
//
//
//        Project projectTest = Project.builder()
//                .projectNo(projectService.getProjectNo(user1))
//                .projectName("test2")
//                .projectStartDate(LocalDateTime.now())
//                .projectStatus(ProjectStatus.IN_PROGRESS)
//                .projectImportance(ProjectImportance.LOW)
//                .clientId(client)
//                .userId(user1).build();
//
//        projectRepository.save(projectTest);
//        //project.setProjectId(projectService.getProjectNo(User user));
//    }
//
////    @Test
////    public void projectMemberInsert(){
////        Client client  = clientRepository.findById(1001L)
////                .orElseThrow(() -> new RuntimeException("거래처를 찿을 수 없습니다"));
////
////        UserEntity user1 = usersRepository.findById(1010L)
////                .orElseThrow(() -> new RuntimeException("유저를 찿을 수 없습니다"));
////
////        String projectStatus = ProjectStatus.BEFORE_START.getStatus();
////
////        Project projectTest = Project.builder()
////                .projectNo(projectService.getProjectNo(user1))
////                .projectName("test6")
////                .projectStartDate(LocalDateTime.now())
////                .projectStatus(ProjectStatus.IN_PROGRESS)
////                .projectImportance(ProjectImportance.LOW)
////                .clientId(client)
////                .userId(user1)
////                .managerName(user1.getUsername())
////                .build();
////
////        projectRepository.save(projectTest);
////
////        UserEntity user2 = usersRepository.findById(1010L)
////                .orElseThrow(() -> new RuntimeException("users를 찿을 수 없습니다"));
////
////        ProjectMemberId projectMemberId = ProjectMemberId
////                .builder()
////                .projectId(projectTest.getProjectId())
////                .userId(user2.getUserId())
////                .build();
////
////        Task task1 = taskRepository.findById(6)
////                .orElseThrow(() -> new RuntimeException("업무를 찿을 수 없습니다."));
////
////        ProjectMember projectMember = ProjectMember
////                .builder()
////                .pmId(projectMemberId)
////                .projectId(projectTest)
////                .userId(user2)
////                .task(task1)
////                .pmRoleName(ProjectMemberStatus.PRODUCT_OWNER)
////                .build();
////
////        projectMemberRepository.save(projectMember);
////
////
////    }
//
//}
//
