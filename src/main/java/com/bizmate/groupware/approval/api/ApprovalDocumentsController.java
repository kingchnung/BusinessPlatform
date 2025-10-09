package com.bizmate.groupware.approval.api;

import com.bizmate.groupware.approval.domain.ApproveRejectRequest;
import com.bizmate.groupware.approval.dto.ApprovalDocumentsDto;
import com.bizmate.groupware.approval.dto.DocumentSearchRequestDto;
import com.bizmate.groupware.approval.service.ApprovalDocumentsService;
import com.bizmate.hr.domain.Users;
import com.bizmate.hr.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approvals")
@Validated
public class ApprovalDocumentsController {

    private final ApprovalDocumentsService approvalDocumentsService;
    private final UserRepository userRepository;

    /* ----------------------------- ① 임시저장 ------------------------------ */
    @PostMapping("/draft")
    public ResponseEntity<ApprovalDocumentsDto> draft(
            @RequestBody ApprovalDocumentsDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws JsonProcessingException {

        log.info("📝 임시저장 요청: {}", dto);

        // ✅ 로그인 사용자 조회
        Long userId = Long.valueOf(userDetails.getUsername());
        Users loginUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다."));

        // ✅ HR 정보 매핑
        dto.setUserId(loginUser.getUserId());
        if (loginUser.getEmployees() != null) {
            dto.setEmpId(loginUser.getEmployees().getEmpId());

            if (loginUser.getEmployees().getDepartments() != null) {
                dto.setDepartmentId(
                        loginUser.getEmployees().getDepartments().getDeptId() != null
                                ? Long.valueOf(loginUser.getEmployees().getDepartments().getDeptId())
                                : null
                );
            }
        }
        dto.setRoleId(loginUser.getPrimaryRoleId());

        // ✅ 서비스 호출
        ApprovalDocumentsDto saved = approvalDocumentsService.draft(dto);
        log.info("✅ 임시저장 완료: {}", saved.getId());
        return ResponseEntity.ok(saved);
    }

    /* ----------------------------- ② 상신 ------------------------------ */
    @PostMapping("/submit")
    public ResponseEntity<ApprovalDocumentsDto> submit(
            @RequestBody ApprovalDocumentsDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws Exception {
        log.info("상신 요청 DTO: {}", dto);

        // ✅ 로그인 사용자 조회
        Users loginUser = userRepository.findById(Long.parseLong(userDetails.getUsername()))
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다."));

        // ✅ HR 필드 매핑
        dto.setUserId(loginUser.getUserId());

        if (loginUser.getEmployees() != null) {
            dto.setEmpId(loginUser.getEmployees().getEmpId());

            if (loginUser.getEmployees().getDepartments() != null) {
                dto.setDepartmentId(
                        Long.valueOf(loginUser.getEmployees().getDepartments().getDeptId())
                );
            }
        }

        dto.setRoleId(loginUser.getPrimaryRoleId());

        // 디버깅용 로그 추가
        log.info("매핑된 HR 정보 → userId={}, empId={}, deptId={}, roleId={}",
                dto.getUserId(), dto.getEmpId(), dto.getDepartmentId(), dto.getRoleId());

        // ✅ 문서 상신 처리
        ApprovalDocumentsDto saved = approvalDocumentsService.submit(dto);
        return ResponseEntity.ok(saved);
    }


    /* ----------------------------- ③ 승인 ------------------------------ */
    @PutMapping("/{docId}/approve")
    public ResponseEntity<ApprovalDocumentsDto> approve(
            @PathVariable String docId,
            @AuthenticationPrincipal UserDetails user) {

        ApprovalDocumentsDto updated = approvalDocumentsService.approve(docId, Long.valueOf(user.getUsername()), null);
        return ResponseEntity.ok(updated);
    }

    /* ----------------------------- ④ 반려 ------------------------------ */
    @PutMapping("/{docId}/reject")
    public ResponseEntity<ApprovalDocumentsDto> reject(
            @PathVariable String docId,
            @AuthenticationPrincipal UserDetails user,
            @RequestBody Map<String, String> body) {

        String reason = body.get("reason");
        ApprovalDocumentsDto updated = approvalDocumentsService.reject(docId, Long.valueOf(user.getUsername()), reason);
        return ResponseEntity.ok(updated);
    }

    /* ----------------------------- ⑤ 논리삭제 ------------------------------ */
    @DeleteMapping("/{docId}")
    public ResponseEntity<Void> logicalDelete(
            @PathVariable String docId,
            @RequestParam @NotNull Long actorUserId,
            @RequestParam(required = false) String reason
    ) {
        approvalDocumentsService.logicalDelete(docId, actorUserId, reason);
        return ResponseEntity.noContent().build();
    }

    /* ----------------------------- ⑥ 조회 ------------------------------ */
    @GetMapping
    public ResponseEntity<List<ApprovalDocumentsDto>> getAllApprovals() {
        return ResponseEntity.ok(approvalDocumentsService.findAllApprovals());
    }

    @GetMapping("/{docId}")
    public ResponseEntity<ApprovalDocumentsDto> get(@PathVariable String docId) {
        return ResponseEntity.ok(approvalDocumentsService.get(docId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ApprovalDocumentsDto>> getMyApprovals(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.valueOf(userDetails.getUsername());
        return ResponseEntity.ok(approvalDocumentsService.findMyApprovals(userId));
    }

    /* ----------------------------- ⑦ 검색 ------------------------------ */
    @GetMapping("/search")
    public ResponseEntity<Page<ApprovalDocumentsDto>> searchApprovals(DocumentSearchRequestDto request) {
        Page<ApprovalDocumentsDto> page = approvalDocumentsService.search(request);
        return ResponseEntity.ok(page);
    }
}
