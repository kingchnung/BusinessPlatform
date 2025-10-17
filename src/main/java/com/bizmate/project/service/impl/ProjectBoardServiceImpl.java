package com.bizmate.project.service.impl;

import com.bizmate.project.domain.ProjectBoard;
import com.bizmate.project.domain.enums.ProjectBoardStatus;
import com.bizmate.project.dto.request.ProjectBoardRequestDTO;
import com.bizmate.project.repository.ProjectBoardRepository;
import com.bizmate.project.repository.ProjectRepository;
import com.bizmate.project.service.ProjectBoardService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectBoardServiceImpl implements ProjectBoardService {

    private final ProjectBoardRepository projectBoardRepository;
    private final ProjectRepository projectRepository;


    private final ModelMapper modelMapper;

    @Override
    public void register(ProjectBoardRequestDTO requestDTO) {
        ProjectBoard projectBoard = modelMapper.map(requestDTO, ProjectBoard.class);
        projectBoard.setBoardType(ProjectBoardStatus.valueOf(requestDTO.getBoardType()));
    }
}
