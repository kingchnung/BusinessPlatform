package com.bizmate.hr.controller;

import com.bizmate.hr.dto.member.LoginRequestDTO;
import com.bizmate.hr.dto.member.ResetPasswordRequest;
import com.bizmate.hr.security.jwt.JWTProvider;
import com.bizmate.hr.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {

    private final AuthService authService;
    private final JWTProvider jWTProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        // 1️⃣ 요청에서 Refresh Token 추출
        String refreshToken = jWTProvider.extractRefreshToken(request);

        if (refreshToken == null) {
            return ResponseEntity.status(400).body(Map.of("error", "Refresh token not found"));
        }

        // 2️⃣ 유효성 검증
        if (!jWTProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token expired or invalid"));
        }

        // 3️⃣ 새 Access Token 생성
        String newAccessToken = jWTProvider.generateAccessTokenFromRefresh(refreshToken);

        // 4️⃣ 프론트로 반환
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest dto) {
        authService.resetPassword(dto);
        return ResponseEntity.ok().body(
                "임시 비밀번호가 등록된 이메일(" + dto.getEmail() + ")로 발송되었습니다.");
    }


}
