package com.bizmate.project.domain;

import com.bizmate.project.domain.auditings.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_post")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "file_post_generator",
sequenceName = "file_post_seq",
initialValue = 1,
allocationSize = 1)
public class FilePost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_post_seq")
    private Integer filePostId;

//    @Column
//    private LocalDateTime fpCreateDate;
//
//    @Column
//    private LocalDateTime fpModifyDate;

    @Lob
    @Column(nullable = false)
    private String fpContent;

    @ManyToOne
    @JoinColumn(name = "project_board_id")
    private ProjectBoard projectBoard;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    })
    private ProjectMember projectMember;


}
