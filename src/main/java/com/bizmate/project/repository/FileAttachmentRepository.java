package com.bizmate.project.repository;

import com.bizmate.project.domain.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileAttachmentRepository extends JpaRepository<FileAttachment,Integer> {
}
