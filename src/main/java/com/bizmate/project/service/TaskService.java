package com.bizmate.project.service;

import com.bizmate.project.dto.request.TaskRequestDTO;
import com.bizmate.project.dto.response.TaskResponseDTO;

public interface TaskService {
    TaskResponseDTO get(Long id);

    Long register(TaskRequestDTO requestDTO);


    void modify(TaskRequestDTO requestDTO, Long id);


    void remove(Long id);
}
