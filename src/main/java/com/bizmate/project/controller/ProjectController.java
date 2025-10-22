package com.bizmate.project.controller;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.project.domain.Project;
import com.bizmate.project.dto.project.ProjectRequestDTO;
import com.bizmate.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    /** ✅ 수동 테스트용 프로젝트 생성 API */
    @PostMapping("/create")
    public Project createProject(@RequestBody ProjectRequestDTO dto) {
        // 실제 전자결재 승인 시 ApprovalDocuments 주입됨
        ApprovalDocuments dummyDoc = new ApprovalDocuments();
        dummyDoc.setDocId("DOC-TEST-001");
        log.info("🧩 프로젝트 수동 생성 요청: {}", dto.getProjectName());
        return projectService.createProjectByApproval(dto, dummyDoc);
    }
}
