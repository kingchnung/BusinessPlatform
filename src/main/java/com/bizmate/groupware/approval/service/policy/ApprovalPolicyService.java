// ApprovalPolicyService.java
package com.bizmate.groupware.approval.service.policy;

import com.bizmate.groupware.approval.domain.policy.ApprovalPolicy;
import com.bizmate.groupware.approval.domain.policy.ApprovalPolicyStep;
import com.bizmate.groupware.approval.dto.approval.ApprovalPolicyRequest;
import com.bizmate.groupware.approval.dto.approval.ApprovalPolicyResponse;
import com.bizmate.groupware.approval.dto.approval.ApprovalPolicyStepRequest;
import com.bizmate.groupware.approval.dto.approval.ApprovalPolicyStepResponse;
import com.bizmate.groupware.approval.repository.Policy.ApprovalPolicyRepository;
import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.code.Position;
import com.bizmate.hr.repository.DepartmentRepository;
import com.bizmate.hr.repository.EmployeeRepository;
import com.bizmate.hr.repository.PositionRepository;
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
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    /** ✅ 결재 정책 생성 */
    @Transactional
    public ApprovalPolicyResponse createPolicy(ApprovalPolicyRequest request) {
        log.info("📄 결재 정책 등록 요청: {}", request.getPolicyName());

        ApprovalPolicy policy = ApprovalPolicy.builder()
                .policyName(request.getPolicyName())
                .docType(request.getDocType())
                .createdBy(request.getCreatedBy())
                .createdDept(request.getCreatedDept())
                .isActive(true)
                .build();

        // ✅ 각 Step 추가
        for (ApprovalPolicyStepRequest dto : request.getSteps()) {

            Department dept = departmentRepository.findByDeptCode(dto.getDeptCode()).orElse(null);

            String positionName = positionRepository.findById(Long.valueOf(dto.getPositionCode()))
                    .map(Position::getPositionName)
                    .orElse("미지정");

            Employee approver = null;
            if (dept != null) {
                approver = employeeRepository.findByDepartmentAndPositionCode(dept, dto.getPositionCode()).orElse(null);
            }

            policy.addStep(ApprovalPolicyStep.builder()
                    .stepOrder(dto.getStepOrder())
                    .deptCode(dto.getDeptCode())
                    .deptName(dept != null ? dept.getDeptName() : dto.getDeptName())
                    .positionCode(dto.getPositionCode())
                    .positionName(positionName)
                    .approver(approver)
                    .build());
        }

        ApprovalPolicy saved = approvalPolicyRepository.save(policy);
        return toResponse(saved);
    }

    /** ✅ 전체 정책 조회 */
    @Transactional
    public List<ApprovalPolicyResponse> getAllPolicies() {
        return approvalPolicyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /** ✅ 정책 비활성화 */
    @Transactional
    public void deactivatePolicy(Long id) {
        ApprovalPolicy policy = approvalPolicyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 정책이 존재하지 않습니다."));
        policy.setActive(false);
    }

    /** ✅ Entity → DTO 변환 */
    private ApprovalPolicyResponse toResponse(ApprovalPolicy policy) {
        return ApprovalPolicyResponse.builder()
                .id(policy.getId())
                .policyName(policy.getPolicyName())
                .docType(policy.getDocType())
                .departmentName(policy.getCreatedDept())
                .isActive(policy.isActive())
                .steps(policy.getSteps().stream()
                        .map(s -> new ApprovalPolicyStepResponse(
                                s.getStepOrder(),
                                s.getDeptName(),
                                s.getPositionName(),
                                s.getApprover() != null ? s.getApprover().getEmpName() : null))
                        .collect(Collectors.toList()))
                .build();
    }
}
