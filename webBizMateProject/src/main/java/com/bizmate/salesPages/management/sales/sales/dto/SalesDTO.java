package com.bizmate.salesPages.management.sales.sales.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesDTO {

    private String salesId;
    private LocalDate salesDate;

    private String projectId;
    private String projectName;
    private LocalDate deploymentDate;
    private BigDecimal salesAmount;
    private String userId;
    private String writer;
    private String clientId;
    private String clientCompany;
    private String salesNote;

    private String invoiceId;
    private boolean invoiceIssued;

    // 연관된 Order 정보는 ID만 포함 (Optional)
    private String orderId;

}
