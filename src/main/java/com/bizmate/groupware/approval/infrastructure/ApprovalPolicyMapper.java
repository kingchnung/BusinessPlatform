package com.bizmate.groupware.approval.infrastructure;

import com.bizmate.groupware.approval.domain.document.Decision;
import com.bizmate.groupware.approval.domain.policy.ApprovalPolicyStep;
import com.bizmate.groupware.approval.domain.policy.ApproverStep;
import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.repository.DepartmentRepository;
import com.bizmate.hr.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
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
    private final DepartmentRepository departmentRepository;

    /**
     * ✅ 정책 단계 목록을 실제 결재선으로 변환
     */
    public List<ApproverStep> toApproverSteps(List<ApprovalPolicyStep> policySteps) {
        if (policySteps == null || policySteps.isEmpty()) {
            log.warn("⚠️ 결재선 정책이 비어 있습니다.");
            return List.of();
        }

        return policySteps.stream()
                .sorted((a, b) -> Integer.compare(a.getStepOrder(), b.getStepOrder()))
                .map(this::mapToApproverStep)
                .filter(step -> step != null)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 단일 단계 변환 로직
     */
    private ApproverStep mapToApproverStep(ApprovalPolicyStep step) {
        if (step == null) return null;

        String deptName = step.getDeptName();
        String positionName = step.getPositionName();

        // ✅ 부서코드 + 직급코드 기반으로 실제 직원 조회
        Department dept = null;
        if (step.getDeptCode() != null) {
            dept = departmentRepository.findByDeptCode(step.getDeptCode()).orElse(null);
        }

        Employee emp = null;
        if (dept != null && step.getPositionCode() != null) {
            emp = employeeRepository.findByDepartmentAndPositionCode(dept, step.getPositionCode()).orElse(null);
        }

        // ✅ 결재자 표시명
        String empName = emp != null ? emp.getEmpName() : "(미지정)";
        String displayName = String.format("%s / %s / %s",
                deptName != null ? deptName : "-",
                positionName != null ? positionName : "-",
                empName);

        if (emp == null) {
            log.warn("⚠️ {} / {} 결재자를 찾을 수 없습니다.", deptName, positionName);
        }

        return new ApproverStep(
                step.getStepOrder(),
                emp != null ? emp.getEmpId().toString() : null, // 결재자 ID
                displayName.trim(),
                Decision.PENDING, // 초기 상태
                "", // 서명 이미지 경로
                null, // 결재일자
                null  // 기타 비고
        );
    }
}
