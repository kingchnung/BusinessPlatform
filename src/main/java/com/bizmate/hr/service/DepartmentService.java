package com.bizmate.hr.service;

import com.bizmate.hr.dto.department.DepartmentDTO;
import com.bizmate.hr.dto.department.DepartmentRequestDTO;

import java.util.List;

public interface DepartmentService {

    /** 부서 등록 및 수정 (ID가 있으면 수정, 없으면 등록) */
    DepartmentDTO saveDepartment(Long deptId, DepartmentRequestDTO requestDTO);

    /** 전체 부서 조회 */
    List<DepartmentDTO> getAllDepartments();

    /** 특정 부서 조회 */
    DepartmentDTO getDepartment(Long deptId);

    /** 부서 삭제 */
    void deleteDepartment(Long deptId);
}