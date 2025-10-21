package com.bizmate.project.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectBudgetItemDTO {
    private String itemName;
    private Long amount;
}