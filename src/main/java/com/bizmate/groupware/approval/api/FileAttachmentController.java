package com.bizmate.groupware.approval.api;

import com.bizmate.common.exception.VerificationFailedException;
import com.bizmate.groupware.approval.domain.*;
import com.bizmate.groupware.approval.repository.*;
import com.bizmate.hr.domain.Users;
import com.bizmate.hr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.nio.file.*;

@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileAttachmentController {

    private final FileAttachmentRepository fileAttachmentRepository;
    private final ApprovalDocumentsRepository approvalDocumentsRepository;
    private final UserRepository userRepository;

    /** ✅ 파일 미리보기 */
    @GetMapping("/preview/{id}")
    public ResponseEntity<Resource> previewFile(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) throws Exception {

        FileAttachment file = fileAttachmentRepository.findById(id)
                .orElseThrow(() -> new VerificationFailedException("첨부파일을 찾을 수 없습니다."));

        checkViewerPermission(file, userDetails);

        Path path = Paths.get(file.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(resource);
    }

    /** ✅ 파일 다운로드 */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) throws Exception {

        FileAttachment file = fileAttachmentRepository.findById(id)
                .orElseThrow(() -> new VerificationFailedException("첨부파일을 찾을 수 없습니다."));

        checkViewerPermission(file, userDetails);

        Path path = Paths.get(file.getFilePath());
        Resource resource = new UrlResource(path.toUri());
        String encodedFileName = new String(file.getOriginalName().getBytes("UTF-8"), "ISO-8859-1");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /** ✅ 첨부파일 열람 권한 확인 */
    private void checkViewerPermission(FileAttachment file, UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());

        Users currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new VerificationFailedException("사용자 정보를 찾을 수 없습니다."));

        ApprovalDocuments doc = file.getDocument();

        boolean isAuthor = doc.getAuthorUser().getUserId().equals(userId);
        boolean isViewer = doc.getViewerIds().contains(String.valueOf(userId));

        // ✅ ApproverStep이 record 타입인 경우 접근 방식 변경
        boolean isApprover = doc.getApprovalLine() != null &&
                doc.getApprovalLine().stream()
                        .anyMatch(step -> step.approverId() != null && step.approverId().equals(userId));

        if (!(isAuthor || isViewer || isApprover)) {
            log.warn("🚫 첨부파일 접근 차단 - 사용자 {} 문서 {} 접근 불가", userId, doc.getDocId());
            throw new VerificationFailedException("이 첨부파일을 열람할 권한이 없습니다.");
        }
    }

}
