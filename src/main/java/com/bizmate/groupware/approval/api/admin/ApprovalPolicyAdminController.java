package com.bizmate.groupware.approval.api.admin;

import com.bizmate.groupware.approval.domain.policy.ApprovalPolicy;
import com.bizmate.groupware.approval.dto.approval.ApprovalPolicyRequest;
import com.bizmate.groupware.approval.service.policy.ApprovalPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/policies")
@RequiredArgsConstructor
public class ApprovalPolicyAdminController {

    private final ApprovalPolicyService approvalPolicyService;

    @PostMapping
    public ResponseEntity<?> createPolicy(@RequestBody ApprovalPolicyRequest request) {
        return ResponseEntity.ok(approvalPolicyService.createPolicy(request));
    }

    @GetMapping
    public ResponseEntity<List<ApprovalPolicy>> getAllPolicies() {

        return ResponseEntity.ok(approvalPolicyService.getAllPolicies());
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivatePolicy(@PathVariable Long id) {
        approvalPolicyService.deactivatePolicy(id);
        return ResponseEntity.ok("정책 비활성화 완료");
    }
}
