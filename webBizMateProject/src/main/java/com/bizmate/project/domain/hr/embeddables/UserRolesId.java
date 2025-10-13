package com.bizmate.project.domain.hr.embeddables;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@Generated
@Setter
@EqualsAndHashCode
public class UserRolesId implements Serializable {

    private Long userId;

    private Long roleId;

    public UserRolesId(Long roleId, Long userId) {
        this.roleId = roleId;
        this.userId = userId;
    }
}
