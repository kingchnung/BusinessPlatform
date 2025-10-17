package com.bizmate.hr.dto.permission;

import com.bizmate.hr.domain.Permission;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionDTO {
    private Long permId;
    private String permName;

    public static PermissionDTO fromEntity(Permission permission) {
        return PermissionDTO.builder()
                .permId(permission.getPermId())
                .permName(permission.getPermName())
                .build();
    }
}
