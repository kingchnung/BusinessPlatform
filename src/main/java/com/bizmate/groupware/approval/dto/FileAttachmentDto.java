package com.bizmate.groupware.approval.dto;

import com.bizmate.groupware.approval.domain.FileAttachment;
import lombok.*;

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

    public static FileAttachmentDto fromEntity(FileAttachment entity) {
        return FileAttachmentDto.builder()
                .id(entity.getId())
                .originalName(entity.getOriginalName())
                .storedName(entity.getStoredName())
                .filePath(entity.getFilePath())
                .fileSize(entity.getFileSize())
                .contentType(entity.getContentType())
                .build();
    }
}
