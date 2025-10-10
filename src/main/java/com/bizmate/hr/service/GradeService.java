package com.bizmate.hr.service;

import com.bizmate.hr.dto.code.GradeDTO;
import com.bizmate.hr.dto.code.GradeRequestDTO;

import java.util.List;

public interface GradeService {
    // 직급코드를 Long 타입으로 받습니다. 신규 등록 시에는 null이 전달됩니다.
    GradeDTO saveGrade(Long gradeCode, GradeRequestDTO requestDTO);

    List<GradeDTO> getAllGrades();

    GradeDTO getGrade(Long gradeCode);

    void deleteGrade(Long gradeCode);
}