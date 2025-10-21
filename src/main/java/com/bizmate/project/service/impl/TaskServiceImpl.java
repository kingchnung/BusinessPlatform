//package com.bizmate.project.service.impl;
//
//import com.bizmate.project.domain.ProjectTask;
//import com.bizmate.project.domain.enums.task.TaskPriority;
//import com.bizmate.project.domain.enums.task.TaskStatus;
//import com.bizmate.project.dto.request.ProjectTaskDTO;
//import com.bizmate.project.dto.response.TaskResponseDTO;
//import com.bizmate.project.repository.ProjectTaskRepository;
//import com.bizmate.project.service.TaskService;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class TaskServiceImpl implements TaskService {
//
//    private final ProjectTaskRepository projectTaskRepository;
//
//    private final ModelMapper modelMapper;
//
//    @Override
//    public TaskResponseDTO get(Long id) {
//        ProjectTask projectTask = projectTaskRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("해당하는 업무를 찾을 수 없습니다."));
//        TaskResponseDTO responseDTO = modelMapper.map(projectTask, TaskResponseDTO.class);
//        return responseDTO;
//    }
//
//    @Override
//    public Long register(ProjectTaskDTO requestDTO) {
//        ProjectTask projectTask = modelMapper.map(requestDTO, ProjectTask.class);
//        updateEnums(projectTask, requestDTO);
//        projectTaskRepository.save(projectTask);
//        return projectTask.getTaskId();
//    }
//
//    @Override
//    public void modify(ProjectTaskDTO requestDTO, Long id) {
//        ProjectTask projectTask = projectTaskRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("해당하는 업무를 찾을 수 없습니다"));
//        modelMapper.map(requestDTO, projectTask);
//        updateEnums(projectTask, requestDTO);
//        projectTaskRepository.save(projectTask);
//    }
//
//    @Override
//    public void remove(Long id) {
//        projectTaskRepository.deleteById(id);
//    }
//
//    private void updateEnums(ProjectTask projectTask, ProjectTaskDTO requestDTO) {
//        try {
//            if (requestDTO.getTaskStatus() != null) {
//                projectTask.setStatus(TaskStatus.valueOf(requestDTO.getTaskStatus()));
//            }
//        } catch (IllegalArgumentException e) {
//            throw new RuntimeException("유효하지 않은 업무 상태 값 입니다. : " + requestDTO.getTaskPriority());
//        }
//
//        try {
//            if (requestDTO.getTaskPriority() != null) {
//                projectTask.setPriority(TaskPriority.valueOf(requestDTO.getTaskPriority()));
//            }
//        } catch (IllegalArgumentException e) {
//            throw new RuntimeException("유효하지 않은 업무 중요도 값 입니다. : " + requestDTO.getTaskPriority());
//        }
//    }
//}
