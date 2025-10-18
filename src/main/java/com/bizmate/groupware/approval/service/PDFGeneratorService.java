package com.bizmate.groupware.approval.service;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.ApproverStep;
import com.bizmate.groupware.approval.repository.ApprovalDocumentsRepository;
import com.bizmate.groupware.approval.repository.EmployeeSignatureRepository;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.repository.EmployeeRepository;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PDFGeneratorService {

    private final ApprovalDocumentsRepository documentsRepository;
    private final EmployeeSignatureRepository employeeSignatureRepository;
    private final PDFSignatureService pdfSignatureService;

    public byte[] generateApprovalPdf(String docId) {
        ApprovalDocuments doc = documentsRepository.findById(docId)
                .orElseThrow(() -> new RuntimeException("문서를 찾을 수 없습니다."));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // ✅ 문서 제목
            Paragraph title = new Paragraph(doc.getTitle())
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);

            // ✅ 문서 기본정보
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 2, 1, 2}))
                    .useAllAvailableWidth();

            infoTable.addCell(cell("문서번호", true));
            infoTable.addCell(cell(doc.getDocId(), false));
            infoTable.addCell(cell("작성자", true));
            infoTable.addCell(cell(doc.getAuthorUser().getEmpName(), false));
            infoTable.addCell(cell("부서명", true));
            infoTable.addCell(cell(doc.getDepartment().getDeptName(), false));
            infoTable.addCell(cell("작성일", true));
            infoTable.addCell(cell(doc.getCreatedAt().toLocalDate().toString(), false));
            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // ✅ 결재란 (전자결재 핵심)
            Table approvalTable = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 3, 2, 4}))
                    .useAllAvailableWidth();

            // 표 헤더
            approvalTable.addHeaderCell(headerCell("순서"));
            approvalTable.addHeaderCell(headerCell("결재자"));
            approvalTable.addHeaderCell(headerCell("상태"));
            approvalTable.addHeaderCell(headerCell("서명"));
            approvalTable.addHeaderCell(headerCell("결재일"));
            approvalTable.addHeaderCell(headerCell("의견"));

            // 본문 행
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (ApproverStep step : doc.getApprovalLine()) {
                approvalTable.addCell(cell(String.valueOf(step.order()), false));
                approvalTable.addCell(cell(step.approverName(), false));
                approvalTable.addCell(cell(step.decision().name(), false));

                // ✅ 서명 이미지 삽입
                Cell signCell = new Cell().setTextAlignment(TextAlignment.CENTER);
                employeeSignatureRepository.findByEmployeeEmpNo(step.approverId()).ifPresent(emp -> {
                    try {
                        Image signImg = pdfSignatureService.getSignatureImage(emp, 60, 40);
                        if (signImg != null) signCell.add(signImg);
                    } catch (Exception e) {
                        log.warn("서명 이미지 로드 실패: {}", emp.getEmployee().getEmpName());
                    }
                });
                approvalTable.addCell(signCell);

                approvalTable.addCell(cell(
                        step.decidedAt() != null ? step.decidedAt().format(formatter) : "-", false
                ));
                approvalTable.addCell(cell(
                        step.comment() != null ? step.comment() : "", false
                ));
            }

            document.add(approvalTable);

            // ✅ 문서 본문 (선택적)
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("문서 내용").setBold().setFontSize(14));
            Map<String, Object> contentMap = doc.getDocContent();
            if (contentMap != null && !contentMap.isEmpty()) {

                Table contentTable = new Table(UnitValue.createPercentArray(new float[]{2, 5}))
                        .useAllAvailableWidth();

                contentTable.addHeaderCell(headerCell("항목"));
                contentTable.addHeaderCell(headerCell("내용"));

                contentMap.forEach((key, value) -> {
                    String displayValue = (value != null) ? value.toString() : "-";
                    contentTable.addCell(cell(key, false));
                    contentTable.addCell(cell(displayValue, false));
                });

                document.add(contentTable);
            } else {
                document.add(new Paragraph("입력된 본문 데이터가 없습니다.").setFontSize(12));
            }
        } catch (Exception e) {
            log.error("❌ PDF 생성 중 오류: {}", e.getMessage(), e);
            throw new RuntimeException("PDF 생성 실패", e);
        }

        return baos.toByteArray();
    }

    private Cell cell(String text, boolean header) {
        Cell c = new Cell().add(new Paragraph(text))
                .setPadding(5)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .setFontSize(10);
        if (header) c.setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold();
        return c;
    }

    private Cell headerCell(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(6);
    }
}
