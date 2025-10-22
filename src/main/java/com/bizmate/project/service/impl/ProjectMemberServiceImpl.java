package com.bizmate.project.service.impl;

import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.repository.UserRepository;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectMember;
import com.bizmate.project.domain.embeddables.ProjectMemberId;
import com.bizmate.project.domain.enums.ProjectMemberStatus;
import com.bizmate.project.dto.request.ProjectMemberRequestDTO;
import com.bizmate.project.dto.response.ProjectMemberResponseDTO;
import com.bizmate.project.repository.ProjectMemberRepository;
import com.bizmate.project.repository.ProjectRepository;
import com.bizmate.project.service.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;


import java.util.List;

import static com.bizmate.project.common.EntityUtils.getEntityOrThrow;


@Service
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {




    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;



    // 멤버 list 조회
    @Override
    public List<ProjectMemberResponseDTO> list(Long projectId) {
        List<ProjectMember> result = projectMemberRepository.findByProjectId_ProjectId(projectId);
        return getDtoList(result);

    }

    // 멤버 등록
    @Override
    public void register(ProjectMemberRequestDTO requestDTO) {
        ProjectMember projectMember = toProjectMember(requestDTO);
        projectMemberRepository.save(projectMember);
    }

    // 단일 멤버 조회
    @Override
    public ProjectMemberResponseDTO get(Long projectId, Long userId) {
        return toDTO(projectId,userId) ;
    }

    // 멤버 수정
    @Override
    public void modify(ProjectMemberRequestDTO requestDTO, Long projectId, Long userId) {
        ProjectMember projectMember = getProjectMember(projectId,userId);
        modelMapper.map(requestDTO, projectMember);

        if (requestDTO.getPmRoleName() != null) {
            projectMember.setPmRoleName(ProjectMemberStatus.valueOf(requestDTO.getPmRoleName()));
        }

        projectMemberRepository.save(projectMember);
    }

    @Override
    public void remove(Long projectId, Long userId) {
        ProjectMemberId id = new ProjectMemberId(projectId,userId);
        projectMemberRepository.deleteById(id);
    }

    private ProjectMember getProjectMember(Long projectId, Long userId){
        ProjectMemberId id = new ProjectMemberId(projectId, userId);

        ProjectMember projectMember = projectMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("수정할 프로젝트 멤버가 존재하지 않습니다."));

        return projectMember;
    }


    // Entity -> ResponseDTO 로 list 변환
    private List<ProjectMemberResponseDTO> getDtoList (List<ProjectMember> list){
        try {
            return list.stream()
                    .map(projectMember -> modelMapper.map(projectMember, ProjectMemberResponseDTO.class))
                    .toList();
        } catch (IllegalArgumentException e){
            throw  new RuntimeException("프로젝트 멤버를 조회할 수 없습니다." , e);
        }
    }


    // RequestDTO -> Entity 변환
    private ProjectMember toProjectMember(ProjectMemberRequestDTO requestDTO){

        ProjectMember projectMember = modelMapper.map(requestDTO, ProjectMember.class);
        projectMember.setPmId(new ProjectMemberId(
                requestDTO.getProjectId(),
                requestDTO.getUserId()
        ));

        projectMember.setPmRoleName(ProjectMemberStatus.valueOf(requestDTO.getPmRoleName()));
        return projectMember;
    }

    // ENTITY -> DTO 변환
    private ProjectMemberResponseDTO toDTO(Long projectId, Long userId){
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId,userId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new RuntimeException("프로젝트 멤버를 조회할 수 없습니다."));

        ProjectMemberResponseDTO responseDTO = modelMapper.map(projectMember, ProjectMemberResponseDTO.class);
        responseDTO.setPmRoleName(projectMember.getPmRoleName().name());

        return responseDTO ;
    }


    private Project getProject(Long id){
            return projectRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("프로젝트 정보를 불러오지 못 했습니다" ));

    }

    private UserEntity getUser(Long id){
            return  userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("사용자를 불러올 수 없습니다."));
    }




}
