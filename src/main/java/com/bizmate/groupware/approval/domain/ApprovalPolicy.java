package com.bizmate.groupware.approval.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "APPROVAL_POLICY")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApprovalPolicy {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String policyName;

    @Column(nullable = false, length = 50)
    private String docType;

    private boolean isActive = true;

    private String createdBy;
    private String createdDept;

    @Builder.Default
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApprovalPolicyStep> steps = new ArrayList<>();

    public void addStep(ApprovalPolicyStep step) {
        step.setPolicy(this);
        this.steps.add(step);
    }
}