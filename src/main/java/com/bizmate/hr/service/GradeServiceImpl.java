package com.bizmate.hr.service;

import com.bizmate.hr.domain.code.Grade;
import com.bizmate.hr.dto.code.GradeDTO;
import com.bizmate.hr.dto.code.GradeRequestDTO;
import com.bizmate.hr.repository.GradeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GradeServiceImpl implements GradeService {

    // 필요한 Repository만 주입
    private final GradeRepository gradeRepository;

    @Override
    public GradeDTO saveGrade(Long gradeCode, GradeRequestDTO requestDTO) {
        Grade grade;

        if (gradeCode != null) {
            // 수정 로직: PK로 기존 엔티티 조회
            grade = gradeRepository.findById(gradeCode)
                    .orElseThrow(() -> new EntityNotFoundException("직급 코드 " + gradeCode + "를 찾을 수 없습니다."));
        } else {
            // 등록 로직: 새 엔티티 생성
            grade = new Grade();
        }

        // DTO 필드 반영
        grade.setGradeName(requestDTO.getGradeName());
        grade.setGradeOrder(requestDTO.getGradeOrder()); // ★ gradeOrder 반영
        // isUsed 필드는 Entity에서 기본값 "Y"가 설정되어 있음

        Grade savedGrade = gradeRepository.save(grade);
        return GradeDTO.fromEntity(savedGrade);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getAllGrades() {
        return gradeRepository.findAll().stream()
                .map(GradeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GradeDTO getGrade(Long gradeCode) {
        Grade grade = gradeRepository.findById(gradeCode)
                .orElseThrow(() -> new EntityNotFoundException("직급 코드 " + gradeCode + "를 찾을 수 없습니다."));
        return GradeDTO.fromEntity(grade);
    }

    @Override
    public void deleteGrade(Long gradeCode) {
        gradeRepository.deleteById(gradeCode);
    }
}