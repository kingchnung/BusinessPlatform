package com.bizmate.salesPages.report.salesReport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySalesSummary {
    private Integer year;
    private Integer month;
    private BigDecimal totalSalesAmount;
}