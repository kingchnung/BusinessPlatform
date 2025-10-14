package com.bizmate.hr.service;

import com.bizmate.hr.domain.Department;
import com.bizmate.hr.dto.department.DepartmentDTO;
import com.bizmate.hr.dto.department.DepartmentRequestDTO;
import com.bizmate.hr.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService { // ★ 서비스 구현체 이름 변경

    private final DepartmentRepository departmentRepository; // ★ Repository 이름 변경

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAllByOrderByDeptCodeAsc().stream()
                .map(DepartmentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartment(Long deptId) {
        Department department = departmentRepository.findById(deptId)
                .orElseThrow(() -> new EntityNotFoundException("부서 ID " + deptId + "를 찾을 수 없습니다."));
        return DepartmentDTO.fromEntity(department);
    }

    @Override
    public DepartmentDTO saveDepartment(Long deptId, DepartmentRequestDTO requestDTO) {
        Department department;
        if (deptId != null) {
            department = departmentRepository.findById(deptId)
                    .orElseThrow(() -> new EntityNotFoundException("부서 ID " + deptId + "를 찾을 수 없습니다."));
        } else {
            department = new Department();
        }

        department.setDeptName(requestDTO.getDeptName());
        department.setDeptCode(requestDTO.getDeptCode());

        Department savedDepartment = departmentRepository.save(department);
        return DepartmentDTO.fromEntity(savedDepartment);
    }

    @Override
    public void deleteDepartment(Long deptId) {
        departmentRepository.deleteById(deptId);
    }
}