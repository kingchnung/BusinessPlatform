package com.bizmate.salesPages.management.order.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private String orderId;
    private LocalDate orderDate;
    private String projectId;
    private String projectName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate orderDueDate;

    private BigDecimal orderAmount;
    private String userId;
    private String writer;
    private String clientId;
    private String clientCompany;
    private String orderNote;
    private String orderStatus;
}
