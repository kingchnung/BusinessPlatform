package com.bizmate.groupware.approval.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record ApproverStep(
        @Min(1)
        int order,              // 결재 순서(1..n)
        @NotBlank
        String approverId,      // 결재자 사용자ID
        Decision decision,      // PENDING/APPROVED/REJECTED
        String comment,         // 코멘트(반려사유 포함)
        LocalDateTime decidedAt
) {}