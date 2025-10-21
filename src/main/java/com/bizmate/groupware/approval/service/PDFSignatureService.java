package com.bizmate.groupware.approval.service;

import com.bizmate.groupware.approval.domain.EmployeeSignature;
import com.bizmate.groupware.approval.repository.EmployeeSignatureRepository;
import com.bizmate.hr.domain.Employee;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class PDFSignatureService {

    private static final String SIGNATURE_PATH = "src/main/resources/signatures/";
    private final EmployeeSignatureRepository employeeSignatureRepository;

    /**
     * 지정한 직원의 서명 이미지를 반환
     */
    /**
     * ✅ 결재란 셀에 서명 이미지 추가
     */
    public void addSignToCell(Cell cell, EmployeeSignature signature) {
        try {
            if (signature == null || signature.getSignImagePath() == null) {
                cell.add(new com.itextpdf.layout.element.Paragraph("미결재")
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.CENTER));
                return;
            }

            File file = new File(signature.getSignImagePath());
            if (!file.exists()) {
                log.warn("⚠️ 서명 파일 없음: {}", signature.getSignImagePath());
                cell.add(new com.itextpdf.layout.element.Paragraph("서명 없음"));
                return;
            }

            ImageData imageData = ImageDataFactory.create(file.getAbsolutePath());
            Image image = new Image(imageData)
                    .setAutoScale(true)
                    .scaleToFit(60, 40) // 서명 크기 조정
                    .setMarginTop(5);

            cell.add(image);
        } catch (Exception e) {
            log.error("❌ 서명 이미지 추가 실패: {}", e.getMessage(), e);
            cell.add(new com.itextpdf.layout.element.Paragraph("오류"));
        }
    }

    /**
     * ✅ 문서 객체(Document)에 직접 서명 추가 (예: 좌표 기반)
     */
    public void addSignToDocument(Document document, Employee employee, float x, float y) {
        employeeSignatureRepository.findByEmployeeEmpNo(employee.getEmpNo())
                .ifPresent(signature -> {
                    try {
                        File file = new File(signature.getSignImagePath());
                        if (file.exists()) {
                            ImageData imageData = ImageDataFactory.create(file.getAbsolutePath());
                            Image image = new Image(imageData)
                                    .scaleToFit(80, 50)
                                    .setFixedPosition(x, y);
                            document.add(image);
                        }
                    } catch (Exception e) {
                        log.error("❌ 문서 내 서명 삽입 실패: {}", e.getMessage());
                    }
                });
    }
}


