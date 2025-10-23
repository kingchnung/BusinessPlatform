package com.bizmate.common.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    // --- ▼ Sales 페이지 검색을 위해 추가된 필드 ▼ ---
    private Integer year;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    private Boolean invoiceIssued;
    private String orderId;


    @Builder.Default
    private String search = "";
    @Builder.Default
    private String keyword = "";

}
