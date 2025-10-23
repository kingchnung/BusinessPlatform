package com.bizmate.project.controller;

import com.bizmate.project.dto.PageResponseDTO;
import com.bizmate.project.dto.request.IssuePostRequestDTO;
import com.bizmate.project.dto.response.IssuePostResponseDTO;
import com.bizmate.project.repository.IssuePostRepository;
import com.bizmate.project.service.IssuePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("bizmate/project/issuePost")
@RequiredArgsConstructor
public class IIssuePostController {

    private final IssuePostService issuePostService;

    public IssuePostResponseDTO get (@PathVariable(name = "id") Long id){
        return issuePostService.get(id);
    }

    @PostMapping("/")
    public Map<String, String > register(@RequestBody IssuePostRequestDTO requestDTO){
        issuePostService.register(requestDTO);
        return Map.of("RESULT","SUCCESS");
    }




}
