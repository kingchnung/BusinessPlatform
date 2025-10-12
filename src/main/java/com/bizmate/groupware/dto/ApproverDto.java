package com.bizmate.groupware.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproverDto {

    private String approverId;
    private int order;
    private String status;
    private String comment;
}
