package com.bizmate.groupware.approval.infrastructure;

import com.bizmate.common.exception.VerificationFailedException;
import com.bizmate.groupware.approval.domain.document.Decision;
import com.bizmate.groupware.approval.domain.policy.ApprovalPolicy;
import com.bizmate.groupware.approval.domain.policy.ApprovalPolicyStep;
import com.bizmate.groupware.approval.domain.policy.ApproverStep;
import com.bizmate.groupware.approval.dto.policy.ApprovalPolicyStepRequest;
import com.bizmate.groupware.approval.dto.policy.ApprovalPolicyStepResponse;
import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.repository.DepartmentRepository;
import com.bizmate.hr.repository.EmployeeRepository;
import com.bizmate.hr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * [ApprovalPolicyMapper]
 * 결재선 정책(ApprovalPolicyStep)을 실제 결재선(ApproverStep) 객체로 변환합니다.
 * 부서코드 + 직급코드를 기반으로 실제 결재자를 매핑합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalPolicyMapper {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public List<ApprovalPolicyStep> toEntities(List<ApprovalPolicyStepRequest> stepRequests, ApprovalPolicy policy) {
        return stepRequests.stream().map(req -> {
            Employee emp = employeeRepository.findById(req.getEmpId())
                    .orElseThrow(() -> new VerificationFailedException("결재자 정보를 찾을 수 없습니다."));

            return ApprovalPolicyStep.builder()
                    .stepOrder(req.getStepOrder())
                    .deptCode(req.getDeptCode())
                    .deptName(emp.getDepartment().getDeptName())
                    .positionCode(req.getPositionCode())
                    .positionName(emp.getPosition().getPositionName())
                    .approver(emp)
                    .approverName(emp.getEmpName()) // ✅ 이름 포함
                    .policy(policy)
                    .build();
        }).collect(Collectors.toList());
    }

    /** 🔹 Entity → Response */
    public List<ApprovalPolicyStepResponse> toResponses(List<ApprovalPolicyStep> steps) {
        return steps.stream()
                .map(s -> ApprovalPolicyStepResponse.builder()
                        .stepOrder(s.getStepOrder())
                        .deptName(s.getDeptName())
                        .positionName(s.getPositionName())
                        .empName(s.getApproverName()) // ✅ 이름 바로 출력
                        .build()
                ).collect(Collectors.toList());
    }
}
