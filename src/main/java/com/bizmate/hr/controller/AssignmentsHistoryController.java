package com.bizmate.hr.controller;


import com.bizmate.hr.dto.assignment.AssignmentHistoryDTO;
import com.bizmate.hr.dto.assignment.AssignmentHistoryRequestDTO;
import com.bizmate.hr.repository.AssignmentsHistoryRepository;
import com.bizmate.hr.service.AssignmentsHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentsHistoryController {

    private final AssignmentsHistoryService assignmentService;

    @GetMapping
    @PreAuthorize("hasAuthority('assignment:read:all') or hasRole('MANAGER')")
    public List<AssignmentHistoryDTO> getAllAssignments() {
        return assignmentService.getAllHistories();
    }

    // 직원 본인 조회
    @GetMapping("/employee/{empId}")
    @PreAuthorize("hasAuthority('assignment:read')")
    public List<AssignmentHistoryDTO> getEmployeeAssignments(@PathVariable Long empId) {
        return assignmentService.getHistoryByEmployee(empId);
    }

    // 발령 등록 (관리자만 가능)
    @PostMapping
    @PreAuthorize("hasAuthority('assignment:create') or hasRole('MANAGER')")
    public ResponseEntity<AssignmentHistoryDTO> createAssignment(
            @RequestBody @Valid AssignmentHistoryRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        AssignmentHistoryDTO created = assignmentService.createAssignment(requestDTO, username);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}

