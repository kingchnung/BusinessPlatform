package com.bizmate.project.service;

import com.bizmate.project.domain.hr.Users;
import com.bizmate.project.domain.sails.Client;

public interface ProjectService {

    public String getProjectNo(Users user);
    public void createProject(Client client);
}
