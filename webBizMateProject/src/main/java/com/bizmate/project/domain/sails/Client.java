package com.bizmate.project.domain.sails;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "client")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Client {

    @Id
    private String clientId;


    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String clientCompany;

    @Column(nullable = false)
    private String clientCeo;

    @Column(nullable = false)
    private String clientBusinessType;

    @Column(nullable = false)
    private String clientAddress;

    @Column(nullable = false)
    private String clientContact;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @Column
    @Lob
    private String clientNote;

    @Column
    private String businessIncenseFile;

    @Column
    private String validationStatus;

    @Column(nullable = false)
    private String empName;

    @Column(nullable = false)
    private String clientEmail;
}
