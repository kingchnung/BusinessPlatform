package com.bizmate.project.domain;

import com.bizmate.project.domain.auditings.BaseTimeEntity;
import com.bizmate.project.domain.enums.ProjectBoardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project_board")
@SequenceGenerator(name = "project_board_generator")
public class ProjectBoard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE ,generator = "project_board_generator")
    private Integer projectBoardId;

    @Column(nullable = false)
    private String boardTitle;

    @Enumerated
    @Column(nullable = false)
    private ProjectBoardStatus boardType = ProjectBoardStatus.ISSUE_BOARD;


    @OneToOne
    @JoinColumn(name = "project_id")
    private Project projectId;
    // forignKey 로 잡은 프로젝트Id

}
