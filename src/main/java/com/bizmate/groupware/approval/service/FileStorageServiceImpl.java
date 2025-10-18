package com.bizmate.groupware.approval.service;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.ApprovalFileAttachment;
import com.bizmate.groupware.approval.repository.ApprovalFileAttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final ApprovalFileAttachmentRepository fileAttachmentRepository;

    // ✅ OS별 경로 맞게 변경 가능
    private static final String UPLOAD_DIR = "C:/bizmate/uploads";

    public ApprovalFileAttachment saveFile(MultipartFile file, ApprovalDocuments document) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("파일이 비어 있습니다.");
            }

            // ✅ 디렉토리 없으면 생성
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // ✅ 원본 이름 & 확장자
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            // ✅ 저장 파일명
            String storedName = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(storedName);

            // ✅ 실제 파일 저장
            Files.copy(file.getInputStream(), filePath);

            // ✅ Content Type 추론
            String contentType = file.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = Files.probeContentType(filePath);
                if (contentType == null) contentType = "application/octet-stream";
            }

            // ✅ DB 저장
            ApprovalFileAttachment attachment = ApprovalFileAttachment.builder()
                    .document(document)
                    .originalName(originalName)
                    .storedName(storedName)
                    .filePath(filePath.toString())
                    .fileSize(file.getSize())
                    .contentType(contentType)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            ApprovalFileAttachment saved = fileAttachmentRepository.save(attachment);
            log.info("✅ 파일 저장 완료: {} ({} bytes, type={})", originalName, file.getSize(), contentType);
            return saved;

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
        }
    }
}
