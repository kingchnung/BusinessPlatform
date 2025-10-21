// ApprovalPolicyStepResponse.java
package com.bizmate.groupware.approval.dto.approval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalPolicyStepResponse {
    private int stepOrder;
    private String deptName;
    private String positionName;
    private String empName;
}
