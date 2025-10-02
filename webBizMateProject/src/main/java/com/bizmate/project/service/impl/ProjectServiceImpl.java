package com.bizmate.project.service.impl;

import com.bizmate.project.domain.hr.Users;
import com.bizmate.project.repository.ProjectRepository;
import com.bizmate.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;

public class ProjectServiceImpl implements ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    @Override
    public String getProjectNo(Users user) {
        int deptId = user.getEmployees().getDepartments().getDeptId();

        Long seqNo = projectRepository.getNextProjectSeq();

        String projectNo = String.format("%d-%04d",deptId,seqNo);

        return projectNo;
    }

    @Override
    public void createProject(Users user ) {

    }


}
