package com.bizmate.project.service;

import com.bizmate.project.dto.request.ProjectTaskDTO;
import com.bizmate.project.dto.response.TaskResponseDTO;

public interface TaskService {
    TaskResponseDTO get(Long id);

    Long register(ProjectTaskDTO requestDTO);


    void modify(ProjectTaskDTO requestDTO, Long id);


    void remove(Long id);
}
