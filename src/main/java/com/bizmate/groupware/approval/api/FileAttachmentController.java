package com.bizmate.groupware.approval.api;

import com.bizmate.common.exception.VerificationFailedException;
import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.ApprovalFileAttachment;
import com.bizmate.groupware.approval.dto.FileAttachmentDto;
import com.bizmate.groupware.approval.repository.ApprovalDocumentsRepository;
import com.bizmate.groupware.approval.repository.ApprovalFileAttachmentRepository;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.repository.UserRepository;
import io.jsonwebtoken.io.IOException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
@Slf4j
public class FileAttachmentController {

    private final EntityManager entityManager;
    private final ApprovalFileAttachmentRepository approvalFileAttachmentRepository;
    private final ApprovalDocumentsRepository approvalDocumentsRepository;
    private final UserRepository userRepository;

    private static final String BASE_UPLOAD_DIR = "C:/bizmate/uploads";

    /**
     * ✅ 1️⃣ 파일 업로드 (문서 ID 포함)
     */
    @PostMapping
    public ResponseEntity<FileAttachmentDto> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "docId", required = false) String docId,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws Exception {

        log.info("📩 파일 업로드 요청: 파일명={}, 문서ID={}", file.getOriginalFilename(), docId);

        // 업로더 조회
        UserEntity uploader = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new VerificationFailedException("사용자 정보를 찾을 수 없습니다."));

        ApprovalDocuments document = null;
        if (docId != null && !docId.isBlank()) {
            document = entityManager.getReference(ApprovalDocuments.class, docId);
        }

        // 실제 저장 경로 생성
        Path uploadDir = Paths.get(BASE_UPLOAD_DIR, LocalDate.now().toString());
        Files.createDirectories(uploadDir);

        String storedName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(storedName);

        //실제 파일 저장
        file.transferTo(filePath.toFile());

        // DB 저장
        ApprovalFileAttachment entity = ApprovalFileAttachment.builder()
                .document(document)               // ✅ 문서가 없으면 null로 저장
                .uploader(uploader)
                .originalName(file.getOriginalFilename())
                .storedName(storedName)
                .filePath(filePath.toString())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .uploadedAt(LocalDateTime.now())
                .build();

        ApprovalFileAttachment saved = approvalFileAttachmentRepository.saveAndFlush(entity);
        FileAttachmentDto dto = FileAttachmentDto.fromEntity(saved);

        log.info("✅ 업로드 완료: {} (문서ID: {})", saved.getOriginalName(), document != null ? document.getDocId() : "임시");
        return ResponseEntity.ok(dto);
    }

    /**
     * ✅ 2️⃣ 문서별 첨부파일 목록 조회
     */
    @GetMapping("/list/{docId}")
    public ResponseEntity<List<FileAttachmentDto>> getFileList(@PathVariable String docId) {
        List<FileAttachmentDto> dtoList = approvalFileAttachmentRepository.findByDocument_DocId(docId)
                .stream()
                .map(FileAttachmentDto::fromEntity)
                .collect(Collectors.toList());

        log.info("📎 문서 [{}] 첨부파일 {}건 반환", docId, dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    // ✅ 미리보기
    @GetMapping("/preview/{id}")
    public ResponseEntity<Resource> preview(@PathVariable Long id) throws IOException {
        ApprovalFileAttachment file = approvalFileAttachmentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("파일을 찾을 수 없습니다."));

        Path path = Paths.get(file.getFilePath());
        Resource resource;

        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new IOException("잘못된 파일 경로 형식입니다: " + file.getFilePath(), e);
        }

        if (!resource.exists()) {
            throw new IllegalStateException("요청한 파일이 존재하지 않습니다: " + file.getFilePath());
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(resource);
    }

    // ✅ 다운로드
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        ApprovalFileAttachment file = approvalFileAttachmentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("파일을 찾을 수 없습니다."));

        Path path = Paths.get(file.getFilePath());
        Resource resource;

        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new IOException("잘못된 파일 경로 형식입니다: " + file.getFilePath(), e);
        }

        if (!resource.exists()) {
            throw new IllegalStateException("요청한 파일이 존재하지 않습니다: " + file.getFilePath());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8) + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
