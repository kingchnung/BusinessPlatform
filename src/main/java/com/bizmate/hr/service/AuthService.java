package com.bizmate.hr.service;

import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.member.LoginRequestDTO;
import com.bizmate.hr.security.UserPrincipal;
import com.bizmate.hr.security.jwt.JWTProvider;
import com.bizmate.hr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * [로그인]
     * - 사용자 인증 후 JWT AccessToken / RefreshToken 발급
     */
    public Map<String, Object> login(LoginRequestDTO request) {
        log.info("🔐 로그인 시도: {}", request.getUsername());

        // 1️⃣ 사용자가 존재하는지 확인
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2️⃣ 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPwHash())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 3️⃣ Authentication 생성 및 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 4️⃣ 인증 성공 후 Principal 추출
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // 5️⃣ JWT 발급
        String accessToken = jwtProvider.createAccessToken(principal);
        String refreshToken = jwtProvider.createRefreshToken(principal);

        // 6️⃣ 응답 데이터 구성
        Map<String, Object> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        tokens.put("username", principal.getUsername());
        tokens.put("roles", principal.getAuthorities());

        log.info("✅ 로그인 성공: {} (토큰 발급 완료)", principal.getUsername());

        return tokens;
    }

    /**
     * [토큰 재발급]
     * - RefreshToken 검증 후 새 AccessToken 발급
     */
    public Map<String, Object> refresh(String refreshToken) {
        log.info("🔄 토큰 재발급 요청");

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
        }

        Authentication authentication = jwtProvider.getAuthentication(refreshToken);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        String newAccessToken = jwtProvider.createAccessToken(principal);

        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", newAccessToken);
        result.put("refreshToken", refreshToken); // 기존 리프레시 유지

        return result;
    }
}
