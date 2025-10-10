package com.bizmate.hr.dto.code;

import com.bizmate.hr.domain.code.Grade;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GradeDTO {
    private Long gradeCode; // ★ 필드명 변경
    private String gradeName;
    private int gradeOrder; // ★ 필드명 변경
    private String isUsed;

    public static GradeDTO fromEntity(Grade grade) {
        return GradeDTO.builder()
                .gradeCode(grade.getGradeCode())
                .gradeName(grade.getGradeName())
                .gradeOrder(grade.getGradeOrder())
                .isUsed(grade.getIsUsed())
                .build();
    }
}
