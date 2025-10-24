package com.bizmate.project.service.impl;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.repository.UserRepository;
import com.bizmate.project.common.EntityUtils;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectMember;
import com.bizmate.project.domain.enums.project.ProjectImportance;
import com.bizmate.project.domain.enums.project.ProjectStatus;
import com.bizmate.project.domain.enums.task.TaskStatus;
import com.bizmate.project.dto.PageRequestDTO;
import com.bizmate.project.dto.PageResponseDTO;
import com.bizmate.project.dto.request.ProjectRequestDTO;
import com.bizmate.project.dto.response.ProjectResponseDTO;
import com.bizmate.project.repository.ProjectRepository;
import com.bizmate.project.service.ProjectService;
import com.bizmate.salesPages.client.domain.Client;
import com.bizmate.salesPages.client.repository.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
//DB 작업을 원자적으로 처리하도록 도와주는 스프링 어노테이션
//정상 → 커밋, 예외 → 롤백 자동 처리
//Service 계층에서 주로 사용
@Slf4j
public class ProjectServiceImpl implements ProjectService {


    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;


    // Request -> Entity 변환 후 DB 등록
    @Override
    public Long register(ProjectRequestDTO requestDTO) {

        Project project = makeProject(requestDTO);
        Project saveProject = projectRepository.save(project);

        return saveProject.getProjectId();
    }

    @Override
    public String getProjectNo(Long id) {
        UserEntity userEntity = EntityUtils.getEntityOrThrow(userRepository,id,"사원");
        Long deptId = userEntity.getEmployee().getDepartment().getDeptId();
        Long seqNo = projectRepository.getNextProjectSeq();
        String projectNo = String.format("%d-%04d", deptId, seqNo);
        return projectNo;
    }

    // 프로젝트 리스트 페이지 렌더링
    @Override
    public PageResponseDTO<ProjectResponseDTO> list(PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("projectNo").descending()
        );

        Page<Project> result = projectRepository.findAll(pageable);
        System.out.println("result = " + result);
        List<ProjectResponseDTO> dtoList = result.getContent().stream().map(
                project -> modelMapper.map(project, ProjectResponseDTO.class)).collect(Collectors.toList());
        System.out.println("dtoList************** = " + dtoList);
        long totalCount = result.getTotalElements();

        PageResponseDTO<ProjectResponseDTO> responseDTO =
                PageResponseDTO
                        .<ProjectResponseDTO>withAll()
                        .dtoList(dtoList)
                        .pageRequestDTO(pageRequestDTO)
                        .totalCount(totalCount)
                        .build();


        return responseDTO;
    }


    @Override
    public ProjectResponseDTO get(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("조회되지 않는 프로젝트 번호 입니다."));
        ProjectResponseDTO responseDTO = modelMapper.map(project, ProjectResponseDTO.class);
        return responseDTO;
    }

    @Override
    public void modify(ProjectRequestDTO requestDTO, Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찿을 수 없습니다."));
        modelMapper.map(requestDTO, project);
        updateEnums(project, requestDTO);
        project.setUserId(getUser(requestDTO.getUserId()));
        projectRepository.save(project);
    }

    @Transactional
    @Override
    public void remove(Long id) {
        projectRepository.deleteById(id);
    }


    // 프로젝트 생성 (전자결재 승인 시 자동 호출)
    @Transactional
    @Override
    public Project createProjectByApproval(ProjectRequestDTO requestDTO, ApprovalDocuments document) {
        log.info("🚀 [프로젝트 자동 생성] 문서ID={}, 프로젝트명={}", document.getDocId(), requestDTO.getProjectName());

        Project project = makeProject(requestDTO);

        // 🔹 참여자 처리 및 Task 담당자 조회를 위한 Map 생성
        // Key: Employee ID, Value: ProjectMember Entity
        

        Project saved = projectRepository.save(project);
        log.info("✅ 프로젝트 생성 완료 (ID: {})", saved.getProjectId());
        return saved;
    }

    // 프로젝트 생성
    private Project makeProject(ProjectRequestDTO requestDTO) {

        Project project = Project.builder()
                .clientId(getClient(requestDTO.getClientId()))
                .userId(getUser(requestDTO.getUserId()))
                .projectName(requestDTO.getProjectName())
                .managerName(getUser(requestDTO.getUserId()).getUsername())
                .build();
        modelMapper.map(requestDTO, project);
        updateEnums(project, requestDTO);

        return project;
    }


    //  거래처 정보 기입
    private Client getClient(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("거래처 정보를 불러올 수 없습니다."));
    }


    // 사용자 정보 기입
    private UserEntity getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 불러올 수 없습니다."));

    }


    // 프로젝트 진행사항, 프로젝트 중요도 String -> ENUM 클래스로 변환
    private void updateEnums(Project project, ProjectRequestDTO requestDTO) {

        try {
            if (requestDTO.getProjectStatus() != null) {
                project.setProjectStatus(ProjectStatus.valueOf(requestDTO.getProjectStatus()));
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 프로젝트 상태 값입니다: " + requestDTO.getProjectStatus(), e);
        }

        try {
            if (requestDTO.getProjectImportance() != null) {
                project.setProjectImportance(ProjectImportance.valueOf(requestDTO.getProjectImportance()));
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 프로젝트 중요도 값입니다: " + requestDTO.getProjectImportance(), e);
        }
    }


}
