package com.bizmate.hr.controller;

import com.bizmate.hr.dto.department.DepartmentDTO;
import com.bizmate.hr.dto.department.DepartmentRequestDTO;
import com.bizmate.hr.service.DepartmentService; // ★ 인터페이스 import 및 사용
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService; // ★ 인터페이스 주입

    @GetMapping
    @PreAuthorize("hasAuthority('dept:read')")
    public List<DepartmentDTO> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('dept:read')")
    public ResponseEntity<DepartmentDTO> getDepartment(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartment(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('dept:create')")
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody @Valid DepartmentRequestDTO requestDTO) {
        DepartmentDTO createdDto = departmentService.saveDepartment(null, requestDTO);
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('dept:update')")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable Long id,
                                                    @RequestBody @Valid DepartmentRequestDTO requestDTO) {
        DepartmentDTO updatedDto = departmentService.saveDepartment(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('dept:delete')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}