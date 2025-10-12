package com.bizmate.project.domain;


import com.bizmate.project.domain.auditings.BaseTimeEntity;
import com.bizmate.project.domain.enums.ProjectStatus;
import com.bizmate.project.domain.hr.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project")
@SequenceGenerator(name = "project_generator",
        sequenceName = "project_seq", initialValue = 1, allocationSize = 1)

public class Project extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_generator")
    private Integer projectId;


    @Column
    private String projectNo;
    // 팀번호+일련번호 4자리 (330001) < 형식으로 만들어 보자

    @Column(name = "project_start_date", nullable = false)
    private LocalDateTime projectStartDate;



    @Column
    @ColumnDefault(value = "sysdate")
    private LocalDateTime projectEndDate;

    @PrePersist
    public void setProjectStartDate() {
        if (projectStartDate == null) {
            projectStartDate = LocalDateTime.now(); // 저장 순간의 당일
        }

        if (projectEndDate == null) {
            projectEndDate = LocalDateTime.now(); // 저장 순간의 당일
        }
    }



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus projectImportance = ProjectStatus.BEFORE_START;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users userId;

}
