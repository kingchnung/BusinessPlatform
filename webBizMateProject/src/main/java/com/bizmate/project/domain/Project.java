package com.bizmate.project.domain;


import com.bizmate.hr.domain.UserEntity;
import com.bizmate.project.domain.auditings.BaseTimeEntity;
import com.bizmate.project.domain.enums.ProjectImportance;
import com.bizmate.project.domain.enums.ProjectStatus;
import com.bizmate.salesPages.client.domain.Client;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project")
@SequenceGenerator(name = "project_id_generator",
    sequenceName = "project_id_seq",
    initialValue = 1,
    allocationSize = 1)
public class Project extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_id_generator")
    private Long projectId;


    @Column(nullable = false)
    private String projectNo;
    // 팀번호+일련번호 4자리 (330001) < 형식으로 만들어 보자

    @Column(nullable = false)
    private String projectName;

    @Column(name = "project_start_date", nullable = false)
    private LocalDateTime projectStartDate;



    @Column
    @ColumnDefault(value = "sysdate")
    private LocalDateTime projectEndDate;





    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectStatus projectStatus = ProjectStatus.BEFORE_START;

    @Enumerated(EnumType.STRING)
    @Column
    private ProjectImportance projectImportance;

    @ManyToOne
    @JoinColumn(name = "user_id" , nullable = false)
    private UserEntity userId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client clientId;

    @Column
    private String managerName;

    @PrePersist
    public void PrePersist() {
        if (projectStartDate == null) {
            projectStartDate = LocalDateTime.now();
            setProjectStartDate(getProjectStartDate().withNano(0));// 저장 순간의 당일
        }

    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectStartDate(LocalDateTime projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    public void setProjectEndDate(LocalDateTime projectEndDate) {
        this.projectEndDate = projectEndDate;
    }

    public void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public void setProjectImportance(ProjectImportance projectImportance) {
        this.projectImportance = projectImportance;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }


}
