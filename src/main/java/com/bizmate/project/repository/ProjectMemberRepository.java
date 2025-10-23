package com.bizmate.project.repository;

import com.bizmate.project.domain.ProjectMember;
import com.bizmate.project.domain.embeddables.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

    @Override
    Optional<ProjectMember> findById(ProjectMemberId projectMemberId);

    List<ProjectMember> findByProjectId_ProjectId(Long projectId);

    boolean existsByProjectId_ProjectIdAndUserId_UserId(Long projectId, Long userId);
}
