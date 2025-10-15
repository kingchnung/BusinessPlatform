package com.bizmate.project.repository;

import com.bizmate.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Query(value = "SELECT project_seq.NEXTVAL FROM DUAL", nativeQuery = true)
    Long getNextProjectSeq();
}
