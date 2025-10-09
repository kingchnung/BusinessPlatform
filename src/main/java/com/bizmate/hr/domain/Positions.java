package com.bizmate.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "positions")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Positions {

    @Id
    private Integer positionCode;


    @Column
    private String positionName;

    @Column
    private String sortOrder;
}
