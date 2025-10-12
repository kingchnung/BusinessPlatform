package com.bizmate.project.domain.hr;

import jakarta.persistence.*;
import lombok.*;

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
    private int userId;

    @Column
    private String username;



    @OneToOne
    @JoinColumn(name ="emp_id")
    private Employees employees;

    @Column
    private String passwordHash;

    @Column
    private String isActiove;

    @Column
    private String isLocked;
}
