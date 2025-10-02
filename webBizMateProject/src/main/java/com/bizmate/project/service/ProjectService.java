package com.bizmate.project.service;

import com.bizmate.project.domain.hr.Users;

public interface ProjectService {

    public String getProjectNo(Users user);
    public void createProject(Users user);
}
