package com.bizmate.hr.dto.permission;

import com.bizmate.hr.domain.Permission;
import lombok.Builder;
import lombok.Getter;

/**
 * [권한 조회 응답 DTO]
 * - Permission 목록 및 상세 정보를 프론트엔드로 전송할 때 사용
 */
@Getter
@Builder
public class PermissionResponseDTO {

    private final Long permiId;
    private final String permiName;
    private final String description;

    /**
     * Entity -> DTO 변환 메서드
     */
    public static PermissionResponseDTO fromEntity(Permission permission) {

        return PermissionResponseDTO.builder()
                .permiId(permission.getPermiId())
                .permiName(permission.getPermiName())
                .description(permission.getDescription())
                .build();
    }
}