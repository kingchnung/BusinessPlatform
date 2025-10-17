package com.bizmate.project.domain;

import com.bizmate.hr.domain.UserEntity;
import com.bizmate.project.domain.auditings.BaseTimeEntity;
import com.bizmate.project.domain.embeddables.ProjectMemberId;
import com.bizmate.project.domain.enums.ProjectMemberStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project_member")
public class ProjectMember extends BaseTimeEntity {


    @EmbeddedId
    private ProjectMemberId pmId;


    @ManyToOne
    @MapsId("userId") // 복합키의 userId 부분을 이 관계로 매핑합니다.
    @JoinColumn(name = "user_id" , nullable = false)
    private UserEntity userId;


    @ManyToOne
    @MapsId("projectId") // 복합키의 userId 부분을 이 관계로 매핑합니다.
    @JoinColumn(name = "project_id", nullable = false)
    private Project projectId;


    @OneToOne
    @JoinColumn(name = "task_id")
    private Task task;


    @Enumerated(EnumType.STRING)
    @Column(name = "pm_role_name", nullable = false)
    @Builder.Default
    private ProjectMemberStatus pmRoleName = ProjectMemberStatus.PRODUCT_OWNER;

    @PrePersist
    public void PrePersist() {




    }



}
