package com.bizmate.groupware.approval.dto;

public record ApprovalPolicyListRecord (
        Long id,
        String policyName,
        String docType,
        String departmentName,
        boolean isActive
){
}
