package com.bizmate.hr.controller;

import com.bizmate.hr.dto.code.PositionDTO;
import com.bizmate.hr.dto.code.PositionRequestDTO;
import com.bizmate.hr.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    /**
     * 직책 전체 조회 API
     * 권한: data:read:all (모든 데이터 조회 가능 권한)
     */
    @GetMapping
    @PreAuthorize("hasAuthority('data:read:all')")
    public List<PositionDTO> getAllPositions() {
        return positionService.getAllPositions();
    }

    /**
     * 신규 직책 등록 API
     * 권한: dept:manage (마스터 데이터 관리 권한)
     */
    @PostMapping
    @PreAuthorize("hasAuthority('dept:manage')")
    public ResponseEntity<PositionDTO> createPosition(@RequestBody @Valid PositionRequestDTO requestDTO) {
        // ID가 null이면 신규 등록 처리
        PositionDTO createdDto = positionService.savePosition(null, requestDTO);
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    /**
     * 직책 정보 수정 API
     * 권한: dept:manage
     */
    @PutMapping("/{code}")
    @PreAuthorize("hasAuthority('dept:manage')")
    public ResponseEntity<PositionDTO> updatePosition(@PathVariable("code") Long code,
                                                      @RequestBody @Valid PositionRequestDTO requestDTO) {
        // PathVariable의 code(PK)를 사용하여 수정 처리
        PositionDTO updatedDto = positionService.savePosition(code, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    /**
     * 직책 삭제 API
     * 권한: dept:manage
     */
    @DeleteMapping("/{code}")
    @PreAuthorize("hasAuthority('dept:manage')")
    public ResponseEntity<Void> deletePosition(@PathVariable("code") Long code) {
        positionService.deletePosition(code);
        return ResponseEntity.noContent().build();
    }
}