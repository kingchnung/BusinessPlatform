package com.bizmate.project.domain;

import com.bizmate.hr.domain.UserEntity;
import com.bizmate.project.domain.auditings.BaseTimeEntity;
import com.bizmate.project.domain.enums.ProjectBoardStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project_board")
@SequenceGenerator(name = "project_board_generator")
public class ProjectBoard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE ,generator = "project_board_generator")
    private Long projectBoardId;

    @Column(nullable = false)
    private String boardTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false ,length = 255)
    @Builder.Default
    private ProjectBoardStatus boardType = ProjectBoardStatus.ISSUE_BOARD;


    @OneToOne
    @JoinColumn(name = "project_id")
    private Project projectId;



}
