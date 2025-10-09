package com.bizmate.groupware.approval.dto;

import com.bizmate.groupware.approval.domain.ApproverStep;
import com.bizmate.groupware.approval.domain.DocumentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDocumentsDto {

    private String id; // 문서 고유 ID

    @NotEmpty(message = "문서 제목은 필수입니다.")
    private String title;

    @NotNull(message = "문서 타입은 필수입니다.")
    private DocumentType docType;

    private String docTypeLabel;
    private String status;

    @NotNull(message = "부서 ID는 필수입니다.")
    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private String finalDocNumber;

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
    private String authorName;

    // ✅ Integer로 변경 (Roles의 PK 타입과 일치)
    @NotNull(message = "역할 ID는 필수입니다.")
    private Integer roleId;

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

    private List<FileAttachmentDto> attachments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String currentApproverId;

    public void setDocType(DocumentType docType) {
        this.docType = docType;
        this.docTypeLabel = (docType != null) ? docType.getLabel() : "-";
    }
}
