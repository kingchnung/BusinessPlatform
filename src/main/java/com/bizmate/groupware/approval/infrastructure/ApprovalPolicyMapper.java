package com.bizmate.groupware.approval.infrastructure;

import com.bizmate.groupware.approval.domain.*;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalPolicyMapper {

    private final EmployeeRepository employeeRepository;

    /**
     * 정책 단계 목록을 실제 결재선으로 변환
     */
    public  List<ApproverStep> toApproverSteps(List<ApprovalPolicyStep> policySteps) {
        if (policySteps == null || policySteps.isEmpty()) {
            log.warn("⚠️ 결재선 정책이 비어 있습니다.");
            return List.of();
        }

        return policySteps.stream()
                .filter(step -> step.getEmpId() != null) // ✅ 등록된 직원만 변환
                .sorted((a, b) -> Integer.compare(a.getStepOrder(), b.getStepOrder()))
                .map(this::mapToApproverStep)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 단일 단계 변환 (empId → Employee)
     */
    private ApproverStep mapToApproverStep(ApprovalPolicyStep step) {
        Employee emp = employeeRepository.findById(step.getEmpId())
                .orElse(null);

        if (emp == null) {
            log.warn("⚠️ 존재하지 않는 직원 ID({}) — 스킵", step.getEmpId());
            return null;
        }

        String deptName = emp.getDepartment() != null ? emp.getDepartment().getDeptName() : "";
        String positionName = emp.getPosition() != null ? emp.getPosition().getPositionName() : "";
        String empName = emp.getEmpName();

        String approverDisplayName = String.join(" ",
                List.of(deptName, positionName, empName).stream()
                        .filter(s -> s != null && !s.isBlank())
                        .toList()
        ).trim();

        return new ApproverStep(
                step.getStepOrder(),
                emp.getEmpId().toString(),
                approverDisplayName,
                Decision.PENDING,
                "",
                null,
                null
        );
    }
}
