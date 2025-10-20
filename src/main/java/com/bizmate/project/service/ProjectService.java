package com.bizmate.project.service;

import com.bizmate.hr.domain.UserEntity;
import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.project.dto.request.ProjectRequestDTO;
import com.bizmate.project.dto.response.ProjectResponseDTO;

public interface ProjectService {

    Long register(ProjectRequestDTO projectRequestDTO);

    String getProjectNo(UserEntity userEntity);

    ProjectResponseDTO get(Long no);

    void modify(ProjectRequestDTO requestDTO, Long id);

    void remove(Long no);

    PageResponseDTO<ProjectResponseDTO> list(PageRequestDTO RequestDTO);
}
