package com.bizmate.groupware.approval.dto.approval;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalPolicyRequest {

    private String policyName;     // 정책명
    private String docType;        // 문서유형코드
    private String createdBy;      // 등록자명
    private String createdDept;    // 등록자부서명

    private List<ApprovalPolicyStepRequest> steps; // 단계별 결재선
}
