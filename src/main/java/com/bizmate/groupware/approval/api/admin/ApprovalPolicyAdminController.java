
package com.bizmate.groupware.approval.api.admin;

import com.bizmate.groupware.approval.dto.approval.ApprovalPolicyRequest;
import com.bizmate.groupware.approval.dto.approval.ApprovalPolicyResponse;
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
    public ResponseEntity<ApprovalPolicyResponse> createPolicy(@RequestBody ApprovalPolicyRequest request) {
        return ResponseEntity.ok(approvalPolicyService.createPolicy(request));
    }

    @GetMapping
    public ResponseEntity<List<ApprovalPolicyResponse>> getAllPolicies() {
        return ResponseEntity.ok(approvalPolicyService.getAllPolicies());
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivatePolicy(@PathVariable Long id) {
        approvalPolicyService.deactivatePolicy(id);
        return ResponseEntity.ok("정책 비활성화 완료");
    }
}
