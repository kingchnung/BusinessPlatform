package com.bizmate.salesPages.report.salesTarget.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
@SequenceGenerator(
        name = "SALES_TARGET_SEQ_GENERATOR",
        sequenceName = "SALES_TARGET_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class SalesTarget {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SALES_TARGET_SEQ_GENERATOR"
    )
    private String targetId;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    private LocalDate registrationDate;

    private Integer targetYear;
    private Integer targetMonth;
    private BigDecimal targetAmount;

    public void changTargetYear(Integer targetYear){
        this.targetYear = targetYear;
    }

    public void changeTargetMonth(Integer targetMonth){
        this.targetMonth = targetMonth;
    }

    public void changeTargetAmount(BigDecimal targetAmount){
        this.targetAmount = targetAmount;
    }
}
