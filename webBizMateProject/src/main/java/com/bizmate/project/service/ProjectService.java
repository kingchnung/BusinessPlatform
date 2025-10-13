package com.bizmate.project.service;

import com.bizmate.hr.domain.UserEntity;
import com.bizmate.project.dto.PageRequestDTO;
import com.bizmate.project.dto.PageResponseDTO;
import com.bizmate.project.dto.request.ProjectRequestDTO;
import com.bizmate.project.dto.response.ProjectResponseDTO;

public interface ProjectService {

    public Long register(ProjectRequestDTO projectRequestDTO);

    public String getProjectNo(UserEntity userEntity);

    public ProjectResponseDTO get(Long no);

    public void modify(ProjectRequestDTO requestDTO, Long id);

    public void remove(Long no);

    public PageResponseDTO<ProjectResponseDTO> list (PageRequestDTO RequestDTO);
}
