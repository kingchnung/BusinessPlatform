package com.bizmate.project.service.impl;

import com.bizmate.project.domain.hr.Users;
import com.bizmate.project.domain.sails.Client;
import com.bizmate.project.repository.ProjectRepository;
import com.bizmate.project.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional
//DB 작업을 원자적으로 처리하도록 도와주는 스프링 어노테이션
//정상 → 커밋, 예외 → 롤백 자동 처리
//Service 계층에서 주로 사용
public class ProjectServiceImpl implements ProjectService {


    private final ProjectRepository projectRepository;

    @Override
    public String getProjectNo(Users user) {
        int deptId = user.getEmployees().getDepartments().getDeptId();
        Long seqNo = projectRepository.getNextProjectSeq();
        String projectNo = String.format("%d-%04d",deptId,seqNo);
        return projectNo;
    }

    @Override
    public void createProject(Client client) {

    }




}
