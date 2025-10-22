package com.bizmate.project.domain;

import com.bizmate.project.domain.auditings.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.sound.midi.Sequence;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "issue_post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "issue_post_generator",
sequenceName = "issue_post_seq",
initialValue = 1,
allocationSize = 1)
public class IssuePost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "issue_post_generator")
    private Integer issuePostId;


    @Column(nullable = false)
    private String ipTitle;

    @Lob
    @Column(nullable = false)
    private String ipContent;

//    @Column
//    private LocalDateTime ipCreateDate;
//
//    @Column
//    private LocalDateTime IpModifyDate;

    //BaseTimeEntity 상속 받아서 사용

    @ManyToOne
    @JoinColumn(name = "project_board_id")
    private ProjectBoard projectBoard;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    })
    private ProjectMember projectMember;

    @PrePersist
    public void PrePersist() {


    }





}