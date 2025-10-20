package com.bizmate.groupware.approval.api.admin;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.groupware.approval.dto.ApprovalDocumentsDto;
import com.bizmate.groupware.approval.service.ApprovalDocumentsService;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/approvals/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ApprovalAdminController {

    private final ApprovalDocumentsService approvalService;

    /* -------------------------------------------------------------
     ✅ 1️⃣ 모든 문서 조회 (관리자 전용)
     ------------------------------------------------------------- */
    @GetMapping("/all")
    public ResponseEntity<PageResponseDTO<ApprovalDocumentsDto>> getAllDocuments(PageRequestDTO pageRequestDTO) {
        log.info("📄 [관리자] 전체 결재문서 조회 요청: page={}, size={}",
                pageRequestDTO.getPage(), pageRequestDTO.getSize(), pageRequestDTO.getKeyword());

        PageResponseDTO<ApprovalDocumentsDto> result =
                approvalService.getPagedApprovals(pageRequestDTO);

        return ResponseEntity.ok(result);
    }

    /* -------------------------------------------------------------
     ✅ 2️⃣ 강제 승인
     ------------------------------------------------------------- */
    @PutMapping("/{docId}/force-approve")
    public ResponseEntity<?> forceApprove(
            @PathVariable String docId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("⚡ [관리자 강제 승인] 문서={}, 관리자={}", docId, principal.getEmpName());

        UserDTO admin = new UserDTO(
                principal.getUserId(),
                principal.getUsername(),
                principal.getEmpName(),
                principal.getEmail(),
                principal.getEmpId()
        );

        approvalService.approve(docId, admin);
        return ResponseEntity.ok("관리자 강제 승인 완료");
    }

    /* -------------------------------------------------------------
     ✅ 3️⃣ 강제 반려
     ------------------------------------------------------------- */
    @PutMapping("/{docId}/force-reject")
    public ResponseEntity<?> forceReject(
            @PathVariable String docId,
            @RequestParam(defaultValue = "관리자 강제 반려") String reason,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("⛔ [관리자 강제 반려] 문서={}, 관리자={}, 사유={}",
                docId, principal.getEmpName(), reason);

        UserDTO admin = new UserDTO(
                principal.getUserId(),
                principal.getUsername(),
                principal.getEmpName(),
                principal.getEmail(),
                principal.getEmpId()
        );

        approvalService.reject(docId, admin, reason);
        return ResponseEntity.ok("관리자 강제 반려 완료");
    }

    /* -------------------------------------------------------------
     ✅ 4️⃣ 결재 로그 조회
     ------------------------------------------------------------- */
    @GetMapping("/{docId}/logs")
    public ResponseEntity<ApprovalDocumentsDto> getDocumentLogs(@PathVariable String docId) {
        log.info("🕓 [관리자] 결재문서 로그 조회 요청: {}", docId);
        ApprovalDocumentsDto dto = approvalService.get(docId);
        return ResponseEntity.ok(dto);
    }

    /* -------------------------------------------------------------
     ✅ 5️⃣ 삭제 문서 복원 (옵션)
     ------------------------------------------------------------- */
    @PutMapping("/{docId}/restore")
    public ResponseEntity<?> restoreDocument(
            @PathVariable String docId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("♻️ [관리자] 문서 복원 요청: {}, 관리자={}", docId, principal.getEmpName());

        approvalService.restoreDocument(docId);
        return ResponseEntity.ok("문서 복원 완료");
    }
}
