package com.bizmate.hr.controller;


import com.bizmate.hr.dto.member.LoginRequestDTO;
import com.bizmate.hr.dto.member.LoginResponseDTO;
import com.bizmate.hr.service.AuthService; // 로그인 로직을 처리할 서비스
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member") // ★ 요청하신 경로 매핑
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final AuthService authService; // 로그인 처리 로직을 담당하는 서비스 주입

    @PostMapping("/login") // ★ 최종 경로: /api/member/login
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO requestDTO) {
        log.info("▶▶▶ 로그인 요청: username={}", requestDTO.getUsername());

        // 1. 서비스 로직 호출 (인증 및 토큰 발급)
        LoginResponseDTO responseDTO = authService.login(requestDTO);

        // 2. 응답 반환
        return ResponseEntity.ok(responseDTO);
    }

    // 이외에 /refresh, /logout 등의 메서드가 추가될 수 있습니다.
}