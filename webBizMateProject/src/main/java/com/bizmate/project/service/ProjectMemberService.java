package com.bizmate.project.service;

import com.bizmate.project.dto.request.ProjectMemberRequestDTO;
import com.bizmate.project.dto.response.ProjectMemberResponseDTO;

import java.util.List;

public interface ProjectMemberService {
    List<ProjectMemberResponseDTO> list(Long projectId);

    void register(ProjectMemberRequestDTO requestDTO);

    ProjectMemberResponseDTO get (Long projectId, Long userId);


    void modify(ProjectMemberRequestDTO requestDTO, Long projectId, Long userId);

    void remove(Long projectId, Long userId);
}
