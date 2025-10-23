package com.bizmate.project.service;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.project.domain.Project;
import com.bizmate.project.dto.project.ProjectRequestDTO;

public interface ProjectService {


    Project createProjectByApproval(ProjectRequestDTO dto, ApprovalDocuments document);


}
