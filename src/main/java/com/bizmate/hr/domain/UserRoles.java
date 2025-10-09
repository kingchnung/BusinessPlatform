package com.bizmate.hr.domain;

import com.bizmate.hr.embeddables.UserRolesId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_role")
public class UserRoles {

    @EmbeddedId
    private UserRolesId userId;



    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id" )
    private Users user;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Roles role;



}
