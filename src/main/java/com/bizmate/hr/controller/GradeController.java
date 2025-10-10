package com.bizmate.hr.controller;

import com.bizmate.hr.dto.code.GradeDTO;
import com.bizmate.hr.dto.code.GradeRequestDTO;
import com.bizmate.hr.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    /**
     * 직급 전체 조회 API
     * 권한: data:read:all
     */
    @GetMapping
    @PreAuthorize("hasAuthority('data:read:all')")
    public List<GradeDTO> getAllGrades() {
        return gradeService.getAllGrades();
    }

    /**
     * 신규 직급 등록 API
     * 권한: dept:manage
     */
    @PostMapping
    @PreAuthorize("hasAuthority('dept:manage')")
    public ResponseEntity<GradeDTO> createGrade(@RequestBody @Valid GradeRequestDTO requestDTO) {
        GradeDTO createdDto = gradeService.saveGrade(null, requestDTO);
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    /**
     * 직급 정보 수정 API
     * 권한: dept:manage
     */
    @PutMapping("/{code}")
    @PreAuthorize("hasAuthority('dept:manage')")
    public ResponseEntity<GradeDTO> updateGrade(@PathVariable("code") Long code,
                                                @RequestBody @Valid GradeRequestDTO requestDTO) {
        GradeDTO updatedDto = gradeService.saveGrade(code, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    /**
     * 직급 삭제 API
     * 권한: dept:manage
     */
    @DeleteMapping("/{code}")
    @PreAuthorize("hasAuthority('dept:manage')")
    public ResponseEntity<Void> deleteGrade(@PathVariable("code") Long code) {
        gradeService.deleteGrade(code);
        return ResponseEntity.noContent().build();
    }
}