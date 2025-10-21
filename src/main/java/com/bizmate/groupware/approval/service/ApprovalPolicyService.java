package com.bizmate.groupware.approval.service;

import com.bizmate.groupware.approval.domain.*;
import com.bizmate.groupware.approval.dto.*;
import com.bizmate.groupware.approval.repository.ApprovalPolicyRepository;
import com.bizmate.groupware.approval.repository.ApprovalPolicyStepRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalPolicyService {

    private final ApprovalPolicyRepository approvalPolicyRepository;

    @Transactional
    public ApprovalPolicy createPolicy(ApprovalPolicyRequest request) {
        log.info("📄 결재선 정책 등록 요청: {}", request.getPolicyName());

        ApprovalPolicy policy = ApprovalPolicy.builder()
                .policyName(request.getPolicyName())
                .docType(request.getDocType())
                .createdBy(request.getCreatedBy())
                .createdDept(request.getCreatedDept())
                .isActive(true)
                .build();

        request.getSteps().forEach(dto -> policy.addStep(
                ApprovalPolicyStep.builder()
                        .stepOrder(dto.getStepOrder())
                        .deptName(dto.getDeptName())
                        .positionCode(dto.getPositionCode())
                        .empId(dto.getEmpId())
                        .build()
        ));

        return approvalPolicyRepository.save(policy);
    }

    public List<ApprovalPolicy> getAllPolicies() {
        return approvalPolicyRepository.findAll();
    }

    @Transactional
    public void deactivatePolicy(Long id) {
        ApprovalPolicy policy = approvalPolicyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 정책이 존재하지 않습니다."));
        policy.setActive(false);
    }
}
