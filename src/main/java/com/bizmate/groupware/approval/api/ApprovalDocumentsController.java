package com.bizmate.groupware.approval.api;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.common.exception.VerificationFailedException;
import com.bizmate.groupware.approval.dto.ApprovalDocumentsDto;
import com.bizmate.groupware.approval.service.ApprovalDocumentsService;
import com.bizmate.hr.dto.user.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalDocumentsController {

    private final ApprovalDocumentsService approvalDocumentsService;

    /* -------------------------------------------------------------
     ✅ 1️⃣ 결재문서 목록 조회 (페이징 + 공통 DTO 구조)
     ------------------------------------------------------------- */
    @GetMapping
    public ResponseEntity<PageResponseDTO<ApprovalDocumentsDto>> getApprovalList(
            PageRequestDTO pageRequestDTO,
            @AuthenticationPrincipal UserDTO loginUser
    ) {
        log.info("📄 결재문서 목록 조회 요청: page={}, size={}, user={}",
                pageRequestDTO.getPage(), pageRequestDTO.getSize(),
                loginUser != null ? loginUser.getUsername() : "anonymous");

        PageResponseDTO<ApprovalDocumentsDto> result =
                approvalDocumentsService.getPagedApprovals(pageRequestDTO);

        return ResponseEntity.ok(result);
    }

    /* -------------------------------------------------------------
     ✅ 2️⃣ 문서 상세 조회
     ------------------------------------------------------------- */
    @GetMapping("/{docId}")
    public ResponseEntity<ApprovalDocumentsDto> getDocumentDetail(@PathVariable String docId) {
        log.info("📋 문서 상세 조회: {}", docId);
        return ResponseEntity.ok(approvalDocumentsService.get(docId));
    }

    /* -------------------------------------------------------------
     ✅ 3️⃣ 문서 임시저장 (Draft)
     ------------------------------------------------------------- */
    @PostMapping("/draft")
    public ResponseEntity<ApprovalDocumentsDto> draftDocument(
            @RequestBody ApprovalDocumentsDto dto,
            @AuthenticationPrincipal UserDTO loginUser) {

        try {
            log.info("💾 [임시저장 요청] {}", dto);
            ApprovalDocumentsDto result = approvalDocumentsService.draft(dto);
            return ResponseEntity.ok(result);
        } catch (VerificationFailedException e) {
            log.warn("🚫 임시저장 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ 임시저장 중 예외 발생", e);
            throw new VerificationFailedException("임시저장 처리 중 오류가 발생했습니다.");
        }
    }

    /* -------------------------------------------------------------
     ✅ 4️⃣ 문서 상신 (Submit)
     ------------------------------------------------------------- */
    @PostMapping("/submit")
    public ResponseEntity<ApprovalDocumentsDto> submitDocument(
            @RequestBody ApprovalDocumentsDto dto,
            @AuthenticationPrincipal UserDTO loginUser) {

        try {
            log.info("🚀 [문서 상신 요청] {}", dto);
            dto.setUserId(loginUser.getUserId());
            dto.setEmpId(loginUser.getEmpId());
            ApprovalDocumentsDto result = approvalDocumentsService.submit(dto);
            return ResponseEntity.ok(result);
        } catch (VerificationFailedException e) {
            log.warn("🚫 상신 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ 상신 중 예외 발생", e);
            throw new VerificationFailedException("문서 상신 처리 중 오류가 발생했습니다.");
        }
    }

    /* -------------------------------------------------------------
 ✅ 5️⃣ 문서 승인 (Approve)
 ------------------------------------------------------------- */
    @PutMapping("/{docId}/approve")
    public ResponseEntity<ApprovalDocumentsDto> approveDocument(
            @PathVariable String docId,
            @AuthenticationPrincipal UserDTO loginUser) {

        try {
            log.info("✅ [문서 승인 요청] 문서ID={}, 승인자={}", docId,
                    loginUser != null ? loginUser.getEmpName() : "anonymous");

            ApprovalDocumentsDto result = approvalDocumentsService.approve(docId, loginUser);
            return ResponseEntity.ok(result);

        } catch (VerificationFailedException e) {
            log.warn("🚫 승인 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ 승인 처리 중 예외 발생", e);
            throw new VerificationFailedException("문서 승인 처리 중 오류가 발생했습니다.");
        }
    }

    /* -------------------------------------------------------------
     ✅ 6️⃣ 문서 반려 (Reject)
     ------------------------------------------------------------- */
    @PutMapping("/{docId}/reject")
    public ResponseEntity<ApprovalDocumentsDto> rejectDocument(
            @PathVariable String docId,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal UserDTO loginUser) {

        try {
            String reason = (body != null) ? body.getOrDefault("reason", "") : "";
            log.info("🔴 반려 요청: docId={}, user={}, reason={}", docId, loginUser.getEmpName(), reason);

            ApprovalDocumentsDto result = approvalDocumentsService.reject(docId, loginUser, reason);
            return ResponseEntity.ok(result);

        } catch (VerificationFailedException e) {
            log.warn("🚫 반려 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ 반려 처리 중 예외 발생", e);
            throw new VerificationFailedException("문서 반려 처리 중 오류가 발생했습니다.");
        }
    }

}
