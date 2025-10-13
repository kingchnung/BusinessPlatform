package com.bizmate.project.repository;

import com.bizmate.project.domain.ProjectBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectBoardRepository extends JpaRepository<ProjectBoard,Integer> {
}
