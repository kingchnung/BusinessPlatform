package com.bizmate.project.domain.hr.embeddables;


import jakarta.persistence.Embeddable;
import lombok.*;

@Generated
@NoArgsConstructor
@Setter
@Getter
@Embeddable
public class RolePermissionsId {


    private Long roleId;

    private Long permiId;

    public RolePermissionsId(Long roleId, Long permiId){
        this.permiId = permiId;
        this.roleId = roleId;
    }


}
