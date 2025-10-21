package com.bizmate.groupware.approval.dto.approval;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalPolicyStepRequest {
    private int stepOrder;       // 결재 순서
    private String deptName;     // 부서명
    private String positionCode; // 직급 코드
    private Long empId;          // 담당자 ID (필요 시)
}
