package com.bizmate.groupware.approval.dto;

import com.bizmate.groupware.approval.domain.*;
import com.bizmate.hr.domain.Department;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 전자결재 문서 DTO
 * - React ↔ Spring 통신용
 * - Entity ↔ DTO 변환 지원
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApprovalDocumentsDto {

    private String id;                // 문서 고유 ID
    private String title;             // 제목
    private DocumentType docType;     // 문서 유형
    private String docTypeLabel;      // 문서 유형 라벨
    private String status;            // 상태 (문자열)

    @NotNull(message = "부서 ID는 필수입니다.")
    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private String finalDocNumber;

    @NotNull(message = "작성자 ID는 필수입니다.")
    private Long userId;
    @JsonProperty("username")
    private String username; //작성자 아이디(사번)
    private String authorName;

    @NotNull(message = "역할 ID는 필수입니다.")
    private Long roleId;

    @NotNull(message = "사번은 필수입니다.")
    private Long empId;

    @Valid
    @NotNull(message = "문서 본문은 필수입니다.")
    @NotEmpty(message = "문서 본문은 비어 있을 수 없습니다.")
    private Map<String, Object> docContent;

    @Valid
    @NotNull(message = "결재선은 필수입니다.")
    @NotEmpty(message = "결재선은 1명 이상이어야 합니다.")
    private List<ApproverStep> approvalLine;

    @Builder.Default
    private List<ApprovalFileAttachmentDto> attachments = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String currentApproverId;

    @Builder.Default
    private List<String> viewerIds = new ArrayList<>();

    public void setDocType(DocumentType docType) {
        this.docType = docType;
        this.docTypeLabel = (docType != null) ? docType.getLabel() : "-";
    }

    /* ==========================================================
       ✅ Entity → DTO 변환
       ========================================================== */
    public static ApprovalDocumentsDto fromEntity(ApprovalDocuments entity) {
        return ApprovalDocumentsDto.builder()
                .id(entity.getDocId())
                .title(entity.getTitle())
                .docType(entity.getDocType())
                .docTypeLabel(entity.getDocType() != null ? entity.getDocType().getLabel() : null)
                .status(entity.getStatus().name())
                .departmentId(entity.getDepartment() != null ? entity.getDepartment().getDeptId() : null)
                .departmentName(entity.getDepartment() != null ? entity.getDepartment().getDeptName() : null)
                .finalDocNumber(entity.getFinalDocNumber())
                .userId(entity.getAuthorUser() != null ? entity.getAuthorUser().getUserId() : null)
                .username(entity.getAuthorUser() != null ? entity.getAuthorUser().getUsername() : null)
                .empId(entity.getAuthorEmployee() != null ? entity.getAuthorEmployee().getEmpId() : null)
                .roleId(entity.getAuthorRole() != null ? entity.getAuthorRole().getRoleId() : null)
                .docContent(entity.getDocContent())
                .approvalLine(entity.getApprovalLine())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .attachments(entity.getAttachments() != null
                        ? entity.getAttachments().stream()
                        .map(ApprovalFileAttachmentDto::fromEntity)
                        .toList()
                        : null)
                .build();
    }

    /* ==========================================================
       ✅ DTO → Entity 변환
       ========================================================== */
    public ApprovalDocuments toEntity(Department department, DocumentStatus status) {
        return ApprovalDocuments.builder()
                .docId(this.id)
                .title(this.title)
                .docType(this.docType)
                .status(status)
                .department(department)
                .finalDocNumber(this.finalDocNumber)
                .approvalLine(this.approvalLine)
                .docContent(this.docContent)
                .currentApproverIndex(0)
                .build();
    }
}
