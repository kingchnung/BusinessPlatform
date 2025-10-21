package com.bizmate.groupware.approval.dto.approval;

import com.bizmate.groupware.approval.domain.policy.ApprovalPolicy;

import java.util.List;

public record ApprovalPolicyResponse(
        Long id,
        String policyName,
        String docType,
        String departmentName,
        List<ApprovalPolicyStepResponse> steps,
        boolean isActive
) {
    public static ApprovalPolicyResponse fromEntity(ApprovalPolicy entity) {
        return new ApprovalPolicyResponse(
                entity.getId(),
                entity.getPolicyName(),
                entity.getDocType(),
                entity.getCreatedDept(),
                entity.getSteps().stream()
                        .map(ApprovalPolicyStepResponse::fromEntity)
                        .collect(Collectors.toList()),
                entity.isActive()
        );
    }
}
