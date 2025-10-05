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
    private int page = 0;
    private int size = 20;
}
