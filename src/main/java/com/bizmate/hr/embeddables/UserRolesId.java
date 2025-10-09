package com.bizmate.hr.embeddables;

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

    private int userId;

    private int roleId;

    public UserRolesId(int roleId, int userId) {
        this.roleId = roleId;
        this.userId = userId;
    }
}
