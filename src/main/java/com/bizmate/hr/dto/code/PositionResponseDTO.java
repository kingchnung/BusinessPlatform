package com.bizmate.hr.dto.code;

import com.bizmate.hr.domain.code.Position;
import lombok.Builder;
import lombok.Getter;

/**
 * [직책 조회 응답 DTO]
 */
@Getter
@Builder
public class PositionResponseDTO {

    private final Long positionCode;
    private final String positionName;
    private final String description;
    private final String isUsed;

    /**
     * Entity -> DTO 변환 메서드
     */
    public static PositionResponseDTO fromEntity(Position position) {
        return PositionResponseDTO.builder()
                .positionCode(position.getPositionCode())
                .positionName(position.getPositionName())
                .description(position.getDescription())
                .isUsed(position.getIsUsed())
                .build();
    }
}