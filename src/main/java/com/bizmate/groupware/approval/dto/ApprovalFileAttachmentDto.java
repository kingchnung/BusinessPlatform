package com.bizmate.groupware.approval.dto;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.ApprovalFileAttachment;
import com.bizmate.hr.domain.UserEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalFileAttachmentDto {
    private Long id;
    private String originalName;
    private String storedName;
    private String filePath;
    private Long fileSize;
    private String contentType;
    private Long uploaderId;
    private String uploaderName;
    private LocalDateTime uploadedAt;

    public static ApprovalFileAttachmentDto fromEntity(ApprovalFileAttachment entity) {
        return ApprovalFileAttachmentDto.builder()
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

    public ApprovalFileAttachment toEntity(ApprovalDocuments document, UserEntity uploader) {
        ApprovalFileAttachment entity = new ApprovalFileAttachment();

        // ID가 이미 존재하면, Hibernate persist 단계에서 예외가 발생하므로
        // → ID는 설정하지 않는다. (merge 시에는 JPA가 알아서 대체)
        if (this.id != null) {
            // ID는 DB에서 영속 attach 시에만 사용 (handleFileAttachments에서 findById)
            // 여긴 신규 생성만 담당
            return null;
        }

        // ✅ 파일 메타정보 설정
        entity.setOriginalName(this.originalName != null ? this.originalName : "unnamed");
        entity.setStoredName(this.storedName != null ? this.storedName : "unknown.tmp");
        entity.setFilePath(this.filePath != null ? this.filePath : "/uploads/unknown");
        entity.setId(this.id);
        entity.setDocument(document);
        entity.setOriginalName(this.originalName);
        entity.setStoredName(this.storedName);
        entity.setFilePath(this.filePath != null ? this.filePath : "N/A");
        entity.setFileSize(this.fileSize != null ? this.fileSize : 0L);
        entity.setContentType(this.contentType != null ? this.contentType : "application/octet-stream");

        // ✅ uploadedAt null 방지 (핵심)
        entity.setUploadedAt(this.uploadedAt != null ? this.uploadedAt : LocalDateTime.now());

        // ✅ 업로더
        if (uploader != null) {
            entity.setUploader(uploader);
        }

        // ✅ 문서 연결
        if (document != null) {
            entity.setDocument(document);
            if (document.getAttachments() != null) {
                document.getAttachments().add(entity);
            } else {
                document.setAttachments(new ArrayList<>(List.of(entity)));
            }
        }

        return entity;
    }
}
