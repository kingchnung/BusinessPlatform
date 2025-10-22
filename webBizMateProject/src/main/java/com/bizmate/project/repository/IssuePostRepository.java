package com.bizmate.project.repository;

import com.bizmate.project.domain.IssuePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuePostRepository extends JpaRepository<IssuePost,Long> {
}
