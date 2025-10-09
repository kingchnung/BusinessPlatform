package com.bizmate.hr.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class Users {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column
    private String username;



    @OneToOne
    @JoinColumn(name ="emp_id")
    private Employees employees;

    @Column
    private String passwordHash;

    @Column
    private String isActive;

    @Column
    private String isLocked;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRoles> userRoles = new ArrayList<>();

    public Roles getPrimaryRole() {
        if (userRoles != null && !userRoles.isEmpty()) {
            return userRoles.getFirst().getRole();
        }
        return null;
    }

    public Integer getPrimaryRoleId() {
        Roles role = getPrimaryRole();
        return (role != null) ? role.getRoleId() : null;
    }

    public String getPrimaryRoleName() {
        Roles role = getPrimaryRole();
        return (role != null) ? role.getRoleName() : null;
    }
}
