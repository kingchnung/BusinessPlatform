package com.bizmate.groupware.approval.dto;

public record ApprovalPolicyStepResponse(
        int stepOrder,
        String deptName,
        String positionName,
        String empName
) {}
