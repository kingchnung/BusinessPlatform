package com.bizmate.hr.controller;

import com.bizmate.hr.dto.employee.EmployeeDTO;
import com.bizmate.hr.dto.employee.EmployeeRequestDTO;
import com.bizmate.hr.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    // ★ 권한 설정: 'emp:read' 권한이 있는 사용자만 접근 가능
    @GetMapping
    @PreAuthorize("hasAuthority('emp:read')")
    public List<EmployeeDTO> getAllEmployees(){
        // ★ 변경: DTO List 반환
        return employeeService.getAllEmployees();
    }

    // ★ 권한 설정: 'emp:read' 권한이 있는 사용자만 접근 가능
    @GetMapping("/{empId}")
    @PreAuthorize("hasAuthority('emp:read')")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long empId){
        // Service가 EntityNotFoundException을 던지므로, Controller는 200 OK 또는 예외 처리를 따릅니다.
        EmployeeDTO dto = employeeService.getEmployee(empId);
        return ResponseEntity.ok(dto);
    }

    // ★ 권한 설정: 'emp:create' 권한이 있는 사용자만 접근 가능
    @PostMapping
    @PreAuthorize("hasAuthority('emp:create')")
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody @Valid EmployeeRequestDTO requestDTO){
        // ★ 변경: RequestDTO를 받고 DTO를 반환
        EmployeeDTO createdDto = employeeService.saveEmployee(null, requestDTO);
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    // ★ 권한 설정: 'emp:update' 권한이 있는 사용자만 접근 가능
    @PutMapping("/{empId}")
    @PreAuthorize("hasAuthority('emp:update')")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long empId,
                                                      @RequestBody @Valid EmployeeRequestDTO requestDTO){
        // ★ 변경: RequestDTO를 받고 DTO를 반환
        EmployeeDTO updatedDto = employeeService.saveEmployee(empId, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    // ★ 권한 설정: 'emp:delete' 권한이 있는 사용자만 접근 가능
    @DeleteMapping("/{empId}")
    @PreAuthorize("hasAuthority('emp:delete')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long empId){
        employeeService.deleteEmployee(empId);
        return ResponseEntity.noContent().build();
    }
}