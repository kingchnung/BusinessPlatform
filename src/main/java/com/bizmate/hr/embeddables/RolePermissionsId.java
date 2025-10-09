package com.bizmate.hr.embeddables;


import jakarta.persistence.Embeddable;
import lombok.*;

@Generated
@NoArgsConstructor
@Setter
@Getter
@Embeddable
public class RolePermissionsId {


    private Integer roleId;

    private Integer permiId;

    public RolePermissionsId(Integer roleId, Integer permiId){
        this.permiId = permiId;
        this.roleId = roleId;
    }


}
