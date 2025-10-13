package com.bizmate.project.domain.hr;

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
    private Long positionCode;


    @Column
    private String positionName;

    @Column
    private String sortOrder;
}
