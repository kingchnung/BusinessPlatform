package com.bizmate.groupware.approval.service;

import com.bizmate.groupware.approval.domain.EmployeeSignature;
import com.bizmate.hr.domain.Employee;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class PDFSignatureService {

    private static final String SIGNATURE_PATH = "src/main/resources/signatures/";

    /**
     * 지정한 직원의 서명 이미지를 반환
     */
    public Image getSignatureImage(EmployeeSignature employee, float width, float height) {
        try {
            if (employee.getSignImagePath() == null) return null;
            File imgFile = new File(SIGNATURE_PATH + employee.getSignImagePath());
            if (!imgFile.exists()) return null;

            Image img = new Image(ImageDataFactory.create(imgFile.getAbsolutePath()));
            img.setAutoScale(true);
            img.setWidth(width);
            img.setHeight(height);
            return img;
        } catch (Exception e) {
            log.warn("서명 이미지 로드 실패: {}", e.getMessage());
            return null;
        }
    }
}
