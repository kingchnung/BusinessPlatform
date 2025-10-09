package com.bizmate.groupware.approval.api;

import com.bizmate.config.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mock")
public class MockAuthController {

    private final JwtTokenProvider jwtTokenProvider;

    public MockAuthController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * ✅ 테스트용 Mock 로그인
     * POST /api/mock/login
     * { "username": "tester" }
     *
     * 반환: JWT 토큰 + 사용자 기본정보
     */
    @PostMapping("/login")
    public ResponseEntity<?> mockLogin(@RequestBody(required = false) Map<String, String> req) {
        String username = (req != null && req.containsKey("username"))
                ? req.get("username")
                : "tester";

        // 인증 객체를 SecurityContext에 수동 주입
        Authentication auth = new UsernamePasswordAuthenticationToken(
                new User(username, "", java.util.List.of()),
                null,
                java.util.List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(username);

        return ResponseEntity.ok(Map.of(
                "username", username,
                "token", token,
                "message", "Mock 로그인 성공"
        ));
    }

    /**
     * ✅ 토큰 검증 API
     * GET /api/mock/verify?token=...
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {
        boolean valid = jwtTokenProvider.validateToken(token);
        String username = valid ? jwtTokenProvider.getUsername(token) : null;
        return ResponseEntity.ok(Map.of(
                "valid", valid,
                "username", username
        ));
    }
}