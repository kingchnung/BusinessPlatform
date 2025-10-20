package com.bizmate.hr.controller;

import com.bizmate.hr.dto.member.LoginRequestDTO;
import com.bizmate.hr.dto.member.ResetPasswordRequest;
import com.bizmate.hr.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {

    private final AuthService authService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public Map<String, Object> refresh(@RequestParam("refreshToken") String refreshToken) {
        return authService.refresh(refreshToken);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest dto) {
        authService.resetPassword(dto);
        return ResponseEntity.ok(Map.of("message", "임시 비밀번호가 이메일로 발송되었습니다."));
    }


}
