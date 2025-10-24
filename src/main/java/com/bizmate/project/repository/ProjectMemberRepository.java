package com.bizmate.project.repository;

import com.bizmate.hr.domain.Employee;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    // ✅ 1. 특정 프로젝트에 속한 모든 멤버 조회
    List<ProjectMember> findByProject(Project project);

    // ✅ 2. 특정 직원(Employee)이 참여 중인 모든 프로젝트 조회
    List<ProjectMember> findByEmployee(Employee employee);

    // ✅ 3. 특정 프로젝트 내 특정 역할(role)을 가진 멤버 조회
    List<ProjectMember> findByProjectAndProjectRole(Project project, String projectRole);

    // ✅ 4. 중복 참여 방지를 위한 검증 (특정 프로젝트에 동일 직원 존재 여부)
    boolean existsByProjectAndEmployee(Project project, Employee employee);
}

