package com.bizmate.project.domain;

import com.bizmate.project.domain.auditings.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_attachment")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator( name = "file_attachment_generator",
        sequenceName = "file_attachment_seq",
        initialValue = 1,
        allocationSize = 1
)
public class FileAttachment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,  generator = "file_attachment_generator")
    private Long fileId;

    @Column(nullable = false)
    private String filePath;


    @ManyToOne
    @JoinColumn(name = "file_post_id")
    private FilePost filePost;

    @PrePersist
    public void PrePersist() {


    }
}
