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
    private Integer fileId;

    @Column(nullable = false)
    private String filePath;

//    @Column
//    private LocalDateTime updateDate;
    // BaseTimeEntity 를 상속받음으로서 자동으로 생성,수정 주기 정리해줌

    @ManyToOne
    @JoinColumn(name = "file_post_id")
    private FilePost filePost;
}
