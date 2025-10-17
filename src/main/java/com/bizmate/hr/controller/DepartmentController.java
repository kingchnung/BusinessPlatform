package com.bizmate.hr.controller;

import com.bizmate.hr.dto.department.*;
import com.bizmate.hr.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 📊 부서현황 조회 (전체 직원 접근 가능)
     */
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/overview")
    public List<DepartmentOverviewDTO> getDepartmentOverview() {
        log.info("📊 부서 현황 조회 요청");
        return departmentService.getDepartmentOverview();
    }

    /**
     * 📋 전체 부서 목록 조회
     */
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping
    public List<DepartmentDTO> getAllDepartments() {
        log.info("📋 전체 부서 목록 조회");
        return departmentService.getAllDepartments();
    }

    /**
     * 📋 부서 상세조회
     */
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/{deptId}")
    public DepartmentResponseDTO getDepartmentDetail(@PathVariable Long deptId) {
        log.info("📋 부서 상세조회 요청: {}", deptId);
        return departmentService.getDepartmentDetail(deptId);
    }

    /**
     * 🏗️ 부서 생성 (관리자 전용)
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public DepartmentResponseDTO createDepartment(@RequestBody DepartmentCreateRequestDTO dto) {
        log.info("🏗️ 부서 생성 요청: {}", dto);
        return departmentService.createDepartment(dto);
    }

    /**
     * ✏️ 부서 수정 (관리자 전용)
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{deptId}")
    public DepartmentResponseDTO updateDepartment(
            @PathVariable Long deptId,
            @RequestBody DepartmentUpdateRequestDTO dto
    ) {
        log.info("✏️ 부서 수정 요청: deptId={}, dto={}", deptId, dto);
        return departmentService.updateDepartment(deptId, dto);
    }

    /**
     * 🗑️ 부서 삭제 (관리자 전용)
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{deptId}")
    public void deleteDepartment(@PathVariable Long deptId) {
        log.info("🗑️ 부서 삭제 요청: {}", deptId);
        departmentService.deleteDepartment(deptId);
    }
}
