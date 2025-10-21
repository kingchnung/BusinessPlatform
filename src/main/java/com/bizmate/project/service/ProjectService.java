package com.bizmate.project.service;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.project.domain.Project;
import com.bizmate.project.dto.request.ProjectRequestDTO;
import com.bizmate.project.dto.response.ProjectResponseDTO;
import jakarta.transaction.Transactional;

public interface ProjectService {


    Project createProject(ProjectRequestDTO dto, ApprovalDocuments document);
}
