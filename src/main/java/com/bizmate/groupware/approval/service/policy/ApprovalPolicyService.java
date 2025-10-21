package com.bizmate.groupware.approval.service.policy;

import com.bizmate.groupware.approval.domain.policy.ApprovalPolicy;
import com.bizmate.groupware.approval.domain.policy.ApprovalPolicyStep;
import com.bizmate.groupware.approval.dto.approval.ApprovalPolicyRequest;
import com.bizmate.groupware.approval.repository.Policy.ApprovalPolicyRepository;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalPolicyService {

    private final ApprovalPolicyRepository approvalPolicyRepository;
    private final EmployeeRepository employeeRepository;

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

        for (ApprovalPolicyStep step : policy.getSteps()) {
            if (step.getDeptName() == null && step.getEmpId() != null) {
                Employee emp = employeeRepository.findById(step.getEmpId()).orElse(null);
                if (emp != null && emp.getDepartment() != null) {
                    step.setDeptName(emp.getDepartment().getDeptName());
                }
            }
        }

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
