package com.bizmate.project.domain;

import com.bizmate.project.domain.auditings.BaseTimeEntity;
import com.bizmate.project.domain.embeddables.ProjectMemberId;
import com.bizmate.project.domain.enums.ProjectMemberStatus;
import com.bizmate.project.domain.hr.UserRoles;
import com.bizmate.project.domain.hr.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project_member")
public class ProjectMember extends BaseTimeEntity {


    @EmbeddedId
    private ProjectMemberId id;


    @ManyToOne
    @MapsId("userId") // 복합키의 userId 부분을 이 관계로 매핑합니다.
    @JoinColumn(name = "user_id")
    private Users userId;


    @ManyToOne
    @MapsId("projectId") // 복합키의 userId 부분을 이 관계로 매핑합니다.
    @JoinColumn(name = "project_id")
    private Project projectId;


    @OneToOne
    @JoinColumn(name = "task_id")
    private Assign assign;


    @Enumerated(EnumType.STRING)
    @Column(name = "pm_role_name")
    private ProjectMemberStatus pmRoleName;

    @PrePersist
    public void PrePersist() {

        if(getRegDate() != null){
            setRegDate(getRegDate().withNano(0));
        }


    }



}
