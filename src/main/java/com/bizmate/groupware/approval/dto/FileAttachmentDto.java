package com.bizmate.groupware.approval.dto;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.ApprovalFileAttachment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileAttachmentDto {
    private Long id;
    private String originalName;
    private String storedName;
    private String filePath;
    private Long fileSize;
    private String contentType;
    private Long uploaderId;
    private String uploaderName;
    private LocalDateTime uploadedAt;

    public static FileAttachmentDto fromEntity(ApprovalFileAttachment entity) {
        return FileAttachmentDto.builder()
                .id(entity.getId())
                .originalName(entity.getOriginalName())
                .storedName(entity.getStoredName())
                .filePath(entity.getFilePath())
                .fileSize(entity.getFileSize())
                .contentType(entity.getContentType())
                .uploadedAt(entity.getUploadedAt()) // ✅ 추가!
                .uploaderId(entity.getUploader() != null ? entity.getUploader().getUserId() : null)
                .uploaderName(entity.getUploader() != null ? entity.getUploader().getEmpName() : "-")
                .build();
    }

    public ApprovalFileAttachment toEntity(ApprovalDocuments document) {
        ApprovalFileAttachment entity = new ApprovalFileAttachment();
        entity.setId(this.id);
        entity.setDocument(document);
        entity.setOriginalName(this.originalName);
        entity.setStoredName(this.storedName);
        entity.setFilePath(this.filePath != null ? this.filePath : "N/A");
        entity.setFileSize(this.fileSize != null ? this.fileSize : 0L);
        entity.setContentType(this.contentType != null ? this.contentType : "application/octet-stream");

        // ✅ uploadedAt null 방지 (핵심)
        entity.setUploadedAt(this.uploadedAt != null ? this.uploadedAt : LocalDateTime.now());

        return entity;
    }
}
