package com.bizmate.groupware.approval.api;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.common.exception.VerificationFailedException;
import com.bizmate.groupware.approval.domain.*;
import com.bizmate.groupware.approval.dto.FileAttachmentDto;
import com.bizmate.groupware.approval.repository.*;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileAttachmentController {

    private final FileAttachmentRepository fileAttachmentRepository;
    private final ApprovalDocumentsRepository approvalDocumentsRepository;
    private final UserRepository userRepository;

    /* ===========================================================
     * ✅ 1️⃣ 파일 목록 조회 (PageRequestDTO / PageResponseDTO 적용)
     * =========================================================== */
    @GetMapping("/list/{docId}")
    public PageResponseDTO<FileAttachmentDto> getFileList(
            @PathVariable String docId,
            PageRequestDTO pageRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        log.info("📂 첨부파일 목록 요청: 문서ID={}, page={}, size={}",
                docId, pageRequestDTO.getPage(), pageRequestDTO.getSize());

        ApprovalDocuments document = approvalDocumentsRepository.findById(docId)
                .orElseThrow(() -> new VerificationFailedException("문서를 찾을 수 없습니다."));

        Long userId = Long.valueOf(userDetails.getUsername());
        UserEntity currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new VerificationFailedException("사용자 정보를 찾을 수 없습니다."));

        // ✅ 접근 권한 확인
        boolean isAuthor = document.getAuthorUser().getUserId().equals(userId);
        boolean isViewer = document.getViewerIds().contains(String.valueOf(userId));
        boolean isApprover = document.getApprovalLine() != null &&
                document.getApprovalLine().stream()
                        .anyMatch(step -> step.approverId() != null && step.approverId().equals(userId));

        if (!(isAuthor || isViewer || isApprover)) {
            log.warn("🚫 첨부파일 목록 접근 차단 - 사용자 {} 문서 {} 접근 불가", userId, docId);
            throw new VerificationFailedException("이 문서의 첨부파일을 조회할 권한이 없습니다.");
        }

        // ✅ 페이징 처리
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<FileAttachment> resultPage =
                fileAttachmentRepository.findByDocument_DocId(docId, pageable);

        List<FileAttachmentDto> dtoList = resultPage.getContent().stream()
                .map(a -> FileAttachmentDto.builder()
                        .id(a.getId())
                        .originalName(a.getOriginalName())
                        .storedName(a.getStoredName())
                        .filePath(a.getFilePath())
                        .fileSize(a.getFileSize())
                        .contentType(a.getContentType())
                        .build())
                .collect(Collectors.toList());

        log.info("📎 첨부파일 {}건 반환 (page {} of {})", dtoList.size(),
                pageRequestDTO.getPage(), resultPage.getTotalPages());

        return PageResponseDTO.<FileAttachmentDto>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(resultPage.getTotalElements())
                .build();
    }

    /* ===========================================================
     * ✅ 2️⃣ 파일 미리보기
     * =========================================================== */
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

    /* ===========================================================
     * ✅ 3️⃣ 파일 다운로드
     * =========================================================== */
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

    /* ===========================================================
     * ✅ 내부 접근 권한 검증 메서드
     * =========================================================== */
    private void checkViewerPermission(FileAttachment file, UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());

        UserEntity currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new VerificationFailedException("사용자 정보를 찾을 수 없습니다."));

        ApprovalDocuments doc = file.getDocument();

        boolean isAuthor = doc.getAuthorUser().getUserId().equals(userId);
        boolean isViewer = doc.getViewerIds().contains(String.valueOf(userId));
        boolean isApprover = doc.getApprovalLine() != null &&
                doc.getApprovalLine().stream()
                        .anyMatch(step -> step.approverId() != null && step.approverId().equals(userId));

        if (!(isAuthor || isViewer || isApprover)) {
            log.warn("🚫 첨부파일 접근 차단 - 사용자 {} 문서 {} 접근 불가", userId, doc.getDocId());
            throw new VerificationFailedException("이 첨부파일을 열람할 권한이 없습니다.");
        }
    }

}
