package com.bizmate.project.repository;

import com.bizmate.project.domain.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectTaskRepository extends JpaRepository<ProjectTask,Long> {
}
