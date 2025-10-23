package com.bizmate.project.repository;

import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.repository.UserRepository;
import com.bizmate.project.common.EntityUtils;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.enums.project.ProjectImportance;
import com.bizmate.project.domain.enums.project.ProjectStatus;
import com.bizmate.project.service.ProjectService;
import com.bizmate.salesPages.client.domain.Client;
import com.bizmate.salesPages.client.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;

@SpringBootTest
public class projectTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ClientRepository clientRepository;


    @Test
    public void createProject(){

        UserEntity user = EntityUtils.getEntityOrThrow(userRepository,5L,"사원");
        Client client = EntityUtils.getEntityOrThrow(clientRepository,21L,"거래처");

        Project project = Project.builder()
                .projectNo(projectService.getProjectNo(5L))
                .projectName("test1")
                .projectStatus(ProjectStatus.BEFORE_START)
                .projectImportance(ProjectImportance.MEDIUM)
                .clientId(client)
                .userId(user)
                .managerName(user.getEmpName())
                .build();

        projectRepository.save(project);


    }

    @Test
    public void readProject(){
        Project project = projectRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("프로젝트를 불러올 수 없습니다,"));

        System.out.println("project.toString() = " + project.toString());
    }

    @Test
    public void modifyProject(){
        Project project = projectRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("프로젝트를 불러올 수 없습니다,"));
        project.setProjectName("수정 테스트");
        projectRepository.save(project);
    }

    @Test
    public void deleteProject(){
        projectRepository.deleteById(2L);
    }

}
