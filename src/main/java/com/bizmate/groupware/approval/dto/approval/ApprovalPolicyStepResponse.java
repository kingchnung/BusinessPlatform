package com.bizmate.groupware.approval.dto.approval;

import com.bizmate.groupware.approval.domain.policy.ApprovalPolicyStep;

public record ApprovalPolicyStepResponse(
        int stepOrder,
        String deptName,
        String positionName,
        String empName
) {
    public static ApprovalPolicyStepResponse fromEntity(ApprovalPolicyStep step) {
        return new ApprovalPolicyStepResponse(
                step.getStepOrder(),
                step.getDeptName(),
                step.getPositionCode(),
                getPositionName(step.getPositionCode()), // ✅ 코드 → 직급명 변환
                step.getEmpId()
        );


    }

    // ✅ 코드값 → 직급명 매핑 로직
    private static String getPositionName(String code) {
        return switch (code) {
            case "1" -> "사원";
            case "2" -> "팀장";
            case "3" -> "부장";
            case "4" -> "CEO";
            default -> code;
        };
    }
}
