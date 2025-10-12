package com.bizmate.salesPages.management.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@ToString
@Entity
@Table(name = "ORDER_MASTER")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    @Id
    @Column(length = 20)
    private String orderId;

    @Column(updatable = false)
    private LocalDate orderDate;

    @Column(nullable = false)
    private Long projectId;

    @Column(length = 100,nullable = false)
    private String projectName;

    private LocalDate orderDueDate;

    @Column(nullable = false)
    private BigDecimal orderAmount;

    @Column(nullable = false)
    private Long employeeId;

    @Builder.Default
    private String orderStatus = "시작전";

    private String orderNote;

    @Column(name = "CREATED_BY", nullable = false)
    private String writer;

    @Column(length = 15, nullable = false)
    private String clientId;

    @Column(length = 100, nullable = false)
    private String clientCompany;

}
