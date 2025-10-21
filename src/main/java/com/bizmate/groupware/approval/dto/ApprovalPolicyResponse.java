package com.bizmate.groupware.approval.dto;

import java.util.List;

public record ApprovalPolicyResponse(
        Long id,
        String policyName,
        String docType,
        String departmentName,
        List<ApprovalPolicyStepResponse> steps,
        boolean isActive
) {}
