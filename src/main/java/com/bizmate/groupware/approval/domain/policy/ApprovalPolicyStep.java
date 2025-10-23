// ApprovalPolicyStep.java
package com.bizmate.groupware.approval.domain.policy;

import com.bizmate.hr.domain.Employee;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "APPROVAL_POLICY_STEP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalPolicyStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int stepOrder;

    private String deptCode;
    private String deptName;

    private String positionCode;
    private String positionName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID")
    private Employee approver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POLICY_ID")
    private ApprovalPolicy policy;
}
