package com.bizmate.groupware.approval.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentSearchRequestDto {
    private String keyword;
    private String status;
    private String fromDate;
    private String toDate;
    private String docType;
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int size = 10;
}
