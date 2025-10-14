package com.bizmate.salesPages.management.sales.salesItem.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SalesItemDTO {
    private Long salesItemId;

    private String itemName;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal vat;
    private BigDecimal totalAmount;
    private String itemNote;
    private Integer lineNum;
}
