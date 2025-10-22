package com.bizmate.project.service;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.project.domain.Project;
import com.bizmate.project.dto.project.ProjectCreateRequest;
import com.bizmate.project.dto.project.ProjectDetailResponse;
import com.bizmate.project.dto.project.ProjectRequestDTO;
import com.bizmate.project.dto.task.ProjectTaskRequest;

public interface ProjectService {


    Project createProjectByApproval(ProjectRequestDTO dto, ApprovalDocuments document);
    //Long createProject(ProjectCreateRequest request, Long authorId);
    //ProjectDetailResponse getProjectDetails(Long projectId);
    //Long addTaskToProject(Long projectId, ProjectTaskRequest request);

}
