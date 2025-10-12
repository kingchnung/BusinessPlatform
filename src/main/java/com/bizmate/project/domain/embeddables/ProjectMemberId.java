package com.bizmate.project.domain.embeddables;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ProjectMemberId  implements Serializable {

    private int projectId;
    private int userId;

    public ProjectMemberId(int projectId, int userId){
        this.projectId = projectId;
        this.userId = userId;
    }


}
