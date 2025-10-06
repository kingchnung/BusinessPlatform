package com.bizmate.groupware.approval.api;

import com.bizmate.groupware.approval.domain.ApproveRejectRequest;
import com.bizmate.groupware.approval.domain.DocumentType;
import com.bizmate.groupware.approval.dto.ApprovalDocumentsDto;
import com.bizmate.groupware.approval.dto.DocumentSearchRequestDto;
import com.bizmate.groupware.approval.service.ApprovalDocumentsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approvals")
@Validated
public class ApprovalDocumentsController {

    private final ApprovalDocumentsService approvalDocumentsService;

    //임시저장
    @PostMapping("/draft")
    public ApprovalDocumentsDto draft(@Valid @RequestBody ApprovalDocumentsDto dto) throws Exception {
        log.info("draft req dto = {}", dto);

        ApprovalDocumentsDto saved = approvalDocumentsService.draft(dto);
        return approvalDocumentsService.draft(saved);
    }

    //상신
    @PostMapping("/submit")
    public ApprovalDocumentsDto submit(@Valid @RequestBody ApprovalDocumentsDto dto) throws Exception {
        ApprovalDocumentsDto saved = approvalDocumentsService.submit(dto);
        return approvalDocumentsService.submit(saved);
    }

    //승인
    @PutMapping("/{docId}/approve")
    public ResponseEntity<ApprovalDocumentsDto> approve(
            @PathVariable String docId,
            @RequestBody @Valid ApproveRejectRequest req
            ) {
        return ResponseEntity.ok(approvalDocumentsService.approve(docId, req.actorUserId(), req.comment()));
    }

    //반려
    @PutMapping("/{docId}/reject")
    public ApprovalDocumentsDto reject(@PathVariable String docId,
                                       @RequestParam Long actorUserId,
                                       @RequestParam String reason) {
        return approvalDocumentsService.reject(docId, actorUserId, reason);
    }

    //논리삭제
    @DeleteMapping("/{docId}")
    public ResponseEntity<Void> logicalDelete(
            @PathVariable String docId,
            @RequestParam @NotNull Long actorUserId,
            @RequestParam(required = false) String reason
    ) {
        approvalDocumentsService.logicalDelete(docId, actorUserId, reason);
        return ResponseEntity.noContent().build();
    }

    //단건 조회
    @GetMapping("/{docId}")
    public ResponseEntity<ApprovalDocumentsDto> get(@PathVariable String docId) {
        return ResponseEntity.ok(approvalDocumentsService.get(docId));
    }

    //검색
    @GetMapping
    public ResponseEntity<Page<ApprovalDocumentsDto>> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false)DocumentType docType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
            ) {
        var req = DocumentSearchRequestDto.builder()
                .status(status)
                .docType(docType.getLabel())
                .keyword(keyword)
                .fromDate(fromDate)
                .toDate(toDate)
                .page(page)
                .size(size)
                .build();
        return ResponseEntity.ok(approvalDocumentsService.search(req));
    }
}
