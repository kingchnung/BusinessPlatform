package com.bizmate.groupware.approval.domain.policy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "APPROVAL_POLICY_STEP")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApprovalPolicyStep {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int stepOrder;
    private String deptName;
    private String positionCode;
    private Long empId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POLICY_ID")
    @JsonBackReference
    private ApprovalPolicy policy;
}

