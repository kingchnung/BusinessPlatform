package com.bizmate.salesPages.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    @Builder.Default
    private String search = "";
    @Builder.Default
    private String keyword = "";

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
