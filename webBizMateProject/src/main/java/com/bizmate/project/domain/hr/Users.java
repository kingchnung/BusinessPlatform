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
@SequenceGenerator(name = "users_id_generator",
        sequenceName = "users_id_seq",
        initialValue = 1011,
        allocationSize = 1)
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_generator")
    private Long userId;

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
