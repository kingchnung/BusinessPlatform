package com.bizmate.project.repository;

import com.bizmate.hr.domain.Department;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.enums.project.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // 1. 특정 부서(Department)가 관리하는 모든 프로젝트 조회
    List<Project> findByDepartment(Department department);

    // 2. 특정 상태(Status)의 모든 프로젝트 조회 (예: '진행중'인 프로젝트만)
    List<Project> findByStatus(ProjectStatus status);
}