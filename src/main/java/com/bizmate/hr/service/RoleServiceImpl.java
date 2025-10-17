package com.bizmate.hr.service;

import com.bizmate.hr.domain.Role;
import com.bizmate.hr.dto.role.RoleDTO;
import com.bizmate.hr.dto.role.RoleRequestDTO;
import com.bizmate.hr.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public RoleDTO saveRole(Long roleId, RoleRequestDTO roleRequestDTO) {
        Role role;
        if(roleId !=  null) {
            role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new EntityNotFoundException("역할 ID " + roleId + "를 찾을 수 없습니다."));
        }else {
            role = new Role();
        }

        role.setRoleName(roleRequestDTO.getRoleName());

        Role savedRole = roleRepository.save(role);
        return RoleDTO.fromEntity(savedRole);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles(){
        return roleRepository.findAll().stream()
                .map(RoleDTO::fromEntity)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public RoleDTO getRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(()->new EntityNotFoundException("역할 ID " + roleId + "를 찾을 수 없습니다."));
        return RoleDTO.fromEntity(role);
    }
}
