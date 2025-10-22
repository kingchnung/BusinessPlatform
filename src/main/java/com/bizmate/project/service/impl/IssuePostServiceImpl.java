package com.bizmate.project.service.impl;

import com.bizmate.project.domain.IssuePost;
import com.bizmate.project.domain.ProjectBoard;
import com.bizmate.project.domain.ProjectMember;
import com.bizmate.project.domain.embeddables.ProjectMemberId;
import com.bizmate.project.dto.request.IssuePostRequestDTO;
import com.bizmate.project.dto.request.ProjectBoardRequestDTO;
import com.bizmate.project.dto.response.IssuePostResponseDTO;
import com.bizmate.project.repository.IssuePostRepository;
import com.bizmate.project.repository.ProjectBoardRepository;
import com.bizmate.project.repository.ProjectMemberRepository;
import com.bizmate.project.service.IssuePostService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssuePostServiceImpl implements IssuePostService {

    private final IssuePostRepository issuePostRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectBoardRepository projectBoardRepository;

    private final ModelMapper modelMapper;


    // 이슈 게시물 저장
    @Override
    public void register(IssuePostRequestDTO requestDTO) {
        IssuePost issuePost = modelMapper.map(requestDTO, IssuePost.class);
        issuePost.setProjectBoard(getBoard(requestDTO));
        issuePost.setProjectMember(getMember(requestDTO));
        issuePostRepository.save(issuePost);
    }

    @Override
    public IssuePostResponseDTO get(Long id) {
        IssuePost issuePost = issuePostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("이슈 게시물을 찿을 수 없습니다."));
        IssuePostResponseDTO responseDTO = modelMapper.map(issuePost, IssuePostResponseDTO.class);
        return responseDTO;
    }

    // 프로젝트 멤버 불러오기
    private ProjectMember getMember(IssuePostRequestDTO requestDTO) {
        ProjectMemberId projectMemberId = ProjectMemberId.builder()
                .projectId(requestDTO.getProjectId())
                .userId(requestDTO.getUserId())
                .build();

        return projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new RuntimeException("멤버를 불러올 수 없습니다."));
    }

    private ProjectBoard getBoard(IssuePostRequestDTO requestDTO) {
        return projectBoardRepository.findById(requestDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("게시판 목록을 불러올 수 없습니다."));
    }
}


