package com.bizmate.salesPages.management.order.orderItem.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemDTO {
    private Long orderItemId;

    private String itemName;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal unitVat;
    private BigDecimal totalAmount;
    private String itemNote;
    private Integer lineNum;
}
