package com.bizmate.groupware.approval.api.admin;

import com.bizmate.groupware.approval.dto.ApprovalPolicyRequest;
import com.bizmate.groupware.approval.service.ApprovalPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getAllPolicies() {
        return ResponseEntity.ok(approvalPolicyService.getAllPolicies());
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivatePolicy(@PathVariable Long id) {
        approvalPolicyService.deactivatePolicy(id);
        return ResponseEntity.ok("정책 비활성화 완료");
    }
}
