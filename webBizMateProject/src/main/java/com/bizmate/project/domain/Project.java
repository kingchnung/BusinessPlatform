package com.bizmate.project.domain;


import com.bizmate.project.domain.auditings.BaseTimeEntity;
import com.bizmate.project.domain.enums.ProjectStatus;
import com.bizmate.project.domain.hr.Users;
import com.bizmate.project.domain.sails.Client;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project")
@SequenceGenerator(name = "project_id_generator",
    sequenceName = "project_id_seq",
    initialValue = 1000,
    allocationSize = 1)
public class Project extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_id_generator")
    private Integer projectId;


    @Column
    private String projectNo;
    // 팀번호+일련번호 4자리 (330001) < 형식으로 만들어 보자

    @Column(nullable = false)
    private String projectName;

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

    }



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectStatus projectStatus = ProjectStatus.BEFORE_START;

    @Column
    private String projectImportance;

    @ManyToOne
    @JoinColumn(name = "user_id" , nullable = false)
    private Users userId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client clientId;

}
