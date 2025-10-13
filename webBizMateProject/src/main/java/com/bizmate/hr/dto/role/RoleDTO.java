package com.bizmate.hr.dto.role;


import com.bizmate.hr.domain.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleDTO {
    private Long roleId;
    private String roleName;

    public static RoleDTO fromEntity(Role role) {
        return RoleDTO.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .build();
    }
}

