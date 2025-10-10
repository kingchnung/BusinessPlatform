package com.bizmate.hr.dto.code;

import com.bizmate.hr.domain.code.Grade;
import lombok.Builder;
import lombok.Getter;

/**
 * [직급 조회 응답 DTO]
 */
@Getter
@Builder
public class GradeResponseDTO {

    private final Long gradeCode;
    private final String gradeName;
    private final Integer gradeOrder;
    private final String isUsed;

    /**
     * Entity -> DTO 변환 메서드
     */
    public static GradeResponseDTO fromEntity(Grade grade) {
        return GradeResponseDTO.builder()
                .gradeCode(grade.getGradeCode())
                .gradeName(grade.getGradeName())
                .gradeOrder(grade.getGradeOrder())
                .isUsed(grade.getIsUsed())
                .build();
    }
}