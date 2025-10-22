package com.bizmate.project.service.impl;

import com.bizmate.project.domain.Task;
import com.bizmate.project.domain.enums.task.TaskPriority;
import com.bizmate.project.domain.enums.task.TaskStatus;
import com.bizmate.project.dto.request.TaskRequestDTO;
import com.bizmate.project.dto.response.TaskResponseDTO;
import com.bizmate.project.repository.TaskRepository;
import com.bizmate.project.service.TaskService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final ModelMapper modelMapper;

    @Override
    public TaskResponseDTO get(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당하는 업무를 찾을 수 없습니다."));
        TaskResponseDTO responseDTO = modelMapper.map(task, TaskResponseDTO.class);
        return responseDTO;
    }

    @Override
    public Long register(TaskRequestDTO requestDTO) {
        Task task = modelMapper.map(requestDTO, Task.class);
        updateEnums(task, requestDTO);
        taskRepository.save(task);
        return task.getTaskId();
    }

    @Override
    public void modify(TaskRequestDTO requestDTO, Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당하는 업무를 찾을 수 없습니다"));
        modelMapper.map(requestDTO, task);
        updateEnums(task, requestDTO);
        taskRepository.save(task);
    }

    @Override
    public void remove(Long id) {
        taskRepository.deleteById(id);
    }

    private void updateEnums(Task task, TaskRequestDTO requestDTO) {
        try {
            if (requestDTO.getTaskStatus() != null) {
                task.setStatus(TaskStatus.valueOf(requestDTO.getTaskStatus()));
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 업무 상태 값 입니다. : " + requestDTO.getTaskPriority());
        }

        try {
            if (requestDTO.getTaskPriority() != null) {
                task.setPriority(TaskPriority.valueOf(requestDTO.getTaskPriority()));
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 업무 중요도 값 입니다. : " + requestDTO.getTaskPriority());
        }
    }
}
