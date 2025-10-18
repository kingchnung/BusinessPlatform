package com.bizmate.groupware.approval.api;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.groupware.approval.repository.ApprovalDocumentsRepository;
import com.bizmate.groupware.approval.service.PDFGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalPDFController {

    private final ApprovalDocumentsRepository documentsRepository;
    private final PDFGeneratorService pdfGeneratorService;

    /**
     * ✅ 문서 PDF 생성 및 미리보기/다운로드
     */
    @GetMapping("/pdf/{docId}")
    public ResponseEntity<ByteArrayResource> previewPdf(
            @PathVariable String docId,
            @RequestParam(defaultValue = "false") boolean download
    ) {
        ApprovalDocuments doc = documentsRepository.findByDocId(docId);

        // ✅ HTML 문서 구성 (간단 예시)
        String html = """
                <html><head>
                    <style>
                        body { font-family: "Malgun Gothic", sans-serif; margin: 30px; }
                        h2 { text-align: center; margin-bottom: 20px; }
                        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
                        th, td { border: 1px solid #444; padding: 8px; text-align: left; }
                        th { background-color: #f8f8f8; }
                        .content { margin-top: 20px; white-space: pre-wrap; }
                    </style>
                </head><body>
                    <h2>%s</h2>
                    <table>
                        <tr><th>문서번호</th><td>%s</td></tr>
                        <tr><th>부서명</th><td>%s</td></tr>
                        <tr><th>작성자</th><td>%s</td></tr>
                        <tr><th>작성일</th><td>%s</td></tr>
                    </table>
                    <div class="content">%s</div>
                </body></html>
                """.formatted(
                doc.getTitle(),
                doc.getDocId(),
                doc.getDepartment().getDeptName(),
                doc.getAuthorUser().getEmpName(),
                doc.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                doc.getDocContent()
        );

        byte[] pdfBytes = pdfGeneratorService.generateApprovalPdf(html);

        String filename = (doc.getTitle() + ".pdf").replaceAll("\\s+", "_");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.builder(download ? "attachment" : "inline")
                        .filename(filename, StandardCharsets.UTF_8)
                        .build()
        );

        return new ResponseEntity<>(new ByteArrayResource(pdfBytes), headers, HttpStatus.OK);
    }
}
