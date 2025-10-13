package com.bizmate.project.repository;

import com.bizmate.project.domain.ProjectMember;
import com.bizmate.project.domain.embeddables.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {
}
