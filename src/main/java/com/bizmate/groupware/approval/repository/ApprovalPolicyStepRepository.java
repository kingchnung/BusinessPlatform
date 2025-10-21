package com.bizmate.groupware.approval.repository;

import com.bizmate.groupware.approval.domain.ApprovalPolicyStep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalPolicyStepRepository extends JpaRepository<ApprovalPolicyStep, Long> {
}
