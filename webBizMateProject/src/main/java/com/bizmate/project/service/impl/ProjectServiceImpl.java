package com.bizmate.project.service.impl;

import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.repository.UserRepository;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.enums.project.ProjectImportance;
import com.bizmate.project.domain.enums.project.ProjectStatus;
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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
//DB 작업을 원자적으로 처리하도록 도와주는 스프링 어노테이션
//정상 → 커밋, 예외 → 롤백 자동 처리
//Service 계층에서 주로 사용
public class ProjectServiceImpl implements ProjectService {


    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;


    // Request -> Entity 변환 후 DB 등록
    @Override
    public Long register(ProjectRequestDTO requestDTO) {


        Client client = clientRepository.findById(requestDTO.getClientId())
                .orElseThrow(() -> new RuntimeException("거래처 정보를 불러올수 없습니다"));
        Project project = Project.builder()
                .clientId(client)
                .build();
        modelMapper.map(requestDTO, project);
        updateEnums(project, requestDTO);
        Project saveProject = projectRepository.save(project);

        return saveProject.getProjectId();
    }

    @Override
    public String getProjectNo(UserEntity userEntity) {
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

    @Override
    public void remove(Long id) {
        projectRepository.deleteById(id);
    }




    private UserEntity getUser(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 불러올 수 없습니다."));
        return userEntity;
    }



    private void updateEnums(Project project, ProjectRequestDTO requestDTO) {

        try {
            if (requestDTO.getProjectStatus() != null) {
                project.setProjectStatus(ProjectStatus.valueOf(requestDTO.getProjectStatus()));
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 프로젝트 상태 값입니다: " + requestDTO.getProjectStatus(),e);
        }

        try {
            if (requestDTO.getProjectImportance() != null) {
                project.setProjectImportance(ProjectImportance.valueOf(requestDTO.getProjectImportance()));
            }
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 프로젝트 중요도 값입니다: " + requestDTO.getProjectImportance(),e);
        }
    }


}
