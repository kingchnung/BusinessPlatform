package com.bizmate.groupware.approval.api;

import com.bizmate.common.exception.VerificationFailedException;
import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.attachment.ApprovalFileAttachment;
import com.bizmate.groupware.approval.dto.approval.ApprovalFileAttachmentDto;
import com.bizmate.groupware.approval.repository.document.ApprovalDocumentsRepository;
import com.bizmate.groupware.approval.repository.attachment.ApprovalFileAttachmentRepository;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/approvals/attachments")
@RequiredArgsConstructor
@Slf4j
public class ApprovalFileAttachmentController {

    private final EntityManager entityManager;
    private final ApprovalFileAttachmentRepository fileAttachmentRepository;
    private final ApprovalDocumentsRepository approvalDocumentsRepository;
    private final UserRepository userRepository;

    private static final String BASE_UPLOAD_DIR = "C:/bizmate/uploads";

    /**
     * ✅ 1️⃣ 파일 업로드 (문서 ID 포함)
     */
    @PostMapping
    public ResponseEntity<ApprovalFileAttachmentDto> uploadFile(
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

        ApprovalFileAttachment saved = fileAttachmentRepository.saveAndFlush(entity);
        ApprovalFileAttachmentDto dto = ApprovalFileAttachmentDto.fromEntity(saved);

        log.info("✅ 업로드 완료: {} (문서ID: {})", saved.getOriginalName(), document != null ? document.getDocId() : "임시");
        return ResponseEntity.ok(dto);
    }

    /**
     * ✅ 2️⃣ 문서별 첨부파일 목록 조회
     */
    @GetMapping("/list/{docId}")
    public ResponseEntity<List<ApprovalFileAttachmentDto>> getFileList(@PathVariable String docId) {
        List<ApprovalFileAttachmentDto> dtoList = fileAttachmentRepository.findByDocument_DocId(docId)
                .stream()
                .map(ApprovalFileAttachmentDto::fromEntity)
                .collect(Collectors.toList());

        log.info("📎 문서 [{}] 첨부파일 {}건 반환", docId, dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    // ✅ 미리보기
    @GetMapping("/preview/{id}")
    public void previewFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
        ApprovalFileAttachment file = fileAttachmentRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("파일이 존재하지 않습니다."));

        File localFile = new File(file.getFilePath());
        if (!localFile.exists()) {
            throw new FileNotFoundException("파일이 존재하지 않습니다.");
        }

        // ✅ Content-Type 설정 (DB 값이 비어있을 때 자동 판별)
        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            String name = file.getOriginalName().toLowerCase();
            if (name.endsWith(".pdf")) contentType = "application/pdf";
            else if (name.endsWith(".png")) contentType = "image/png";
            else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) contentType = "image/jpeg";
            else contentType = "application/octet-stream";
        }
        response.setContentType(contentType);

        // ✅ inline 미리보기 지원
        if (contentType.startsWith("image/") || contentType.equals("application/pdf")) {
            response.setHeader("Content-Disposition",
                    "inline; filename=\"" + URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8) + "\"");
        } else {
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8) + "\"");
        }

        // ✅ 캐시 방지 (안 하면 이전 파일로 미리보기 뜨는 경우 있음)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // ✅ 스트리밍 전송
        try (InputStream in = new BufferedInputStream(new FileInputStream(localFile));
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }
    }


    // ✅ 다운로드
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, HttpServletResponse response) {
        ApprovalFileAttachment file = fileAttachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다. ID=" + id));

        File localFile = new File(file.getFilePath());
        if (!localFile.exists()) {
            throw new RuntimeException("저장된 파일이 존재하지 않습니다: " + file.getFilePath());
        }

        try {
            String encodedName = URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20"); // 공백 처리

            FileSystemResource resource = new FileSystemResource(localFile);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(
                    ContentDisposition.attachment().filename(encodedName, StandardCharsets.UTF_8).build());
            headers.setContentType(MediaType.parseMediaType(file.getContentType() != null
                    ? file.getContentType()
                    : "application/octet-stream"));
            headers.setContentLength(localFile.length());

            log.info("📥 파일 다운로드 요청: {}", file.getOriginalName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            log.error("❌ 파일 다운로드 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
