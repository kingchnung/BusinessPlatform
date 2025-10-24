package com.bizmate.project.service;

import com.bizmate.project.dto.request.IssuePostRequestDTO;
import com.bizmate.project.dto.response.IssuePostResponseDTO;

public interface IssuePostService {
    void register(IssuePostRequestDTO requestDTO);

    IssuePostResponseDTO get(Long id);
}
