package com.bizmate.project.controller;

import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.sails.Client;
import com.bizmate.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/bizmate/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;


    public void insertProject(Client client){
        projectService.createProject(client);
    }

}
