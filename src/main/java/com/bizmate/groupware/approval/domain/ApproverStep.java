package com.bizmate.groupware.approval.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

public record ApproverStep(
        @Min(1)
        @JsonProperty("order")
        int order,              // 결재 순서(1..n)
        @JsonProperty("approverId")
        @NotNull
        String approverId,      // 결재자 사용자ID
        @JsonProperty("approverName")
        @NotBlank
        String approverName,
        @JsonProperty("decision")
        Decision decision,      // PENDING/APPROVED/REJECTED
        @JsonProperty("comment")
        String comment,         // 코멘트(반려사유 포함)
        @JsonProperty("decidedAt")
        LocalDateTime decidedAt
) {}