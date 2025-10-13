package com.bizmate.hr.service;

import com.bizmate.hr.dto.role.RoleDTO;
import com.bizmate.hr.dto.role.RoleRequestDTO;

import java.util.List;

public interface RoleService {
    RoleDTO saveRole(Long roleId, RoleRequestDTO roleRequestDTO);
    List<RoleDTO> getAllRoles();
    RoleDTO getRole(Long roleId);
}
