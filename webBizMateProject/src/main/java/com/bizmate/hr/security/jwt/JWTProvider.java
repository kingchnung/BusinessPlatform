package com.bizmate.hr.security.jwt;

import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.user.UserDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.nio.charset.StandardCharsets;

/**
 * [JWTProvider]
 * - JWT 생성, 검증 및 정보 추출을 담당하는 클래스.
 * - ★★★ 실습 환경 효율을 위해 설정값을 코드 내부에 작성 (운영 환경에서는 외부 설정 사용 필수) ★★★
 */
@Component
@Slf4j
public class JWTProvider {

    // ★★★ 1. 설정값 (코드 내장) ★★★
    // 비밀 키: 보안상 32바이트 이상 권장. (테스트용)
    private static final String SECRET_KEY = "1234567890123456789012345678901234567890";
    private static final long ACCESS_EXP_TIME = 10; // Access Token 유효 시간 (분)
    private static final long REFRESH_EXP_TIME_DAYS = 1; // Refresh Token 유효 시간 (일)
    // ★★★ --------------------- ★★★

    private final SecretKey key;

    public JWTProvider() {
        // 비밀 키 초기화 및 Base64 디코딩 (HMAC SHA-256 서명 키 생성)
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        log.info("JWTProvider 초기화 완료. Access Exp: {}분, Refresh Exp: {}일",
                ACCESS_EXP_TIME, REFRESH_EXP_TIME_DAYS);
    }

    // --- 1. 토큰 생성 메서드 ---

    /**
     * Access Token을 생성합니다.
     */
    public String createAccessToken(UserDTO userDTO, List<String> roles, List<String> perms) { // ★ 시그니처 수정
        // 클레임 구성: UserEntity와 roles/perms를 사용
        Map<String, Object> claims = Map.of(
                "userId", userDTO.getUserId(),
                "username", userDTO.getUsername(),
                "empId", userDTO.getEmpId(), // UserEntity에서 Employee 정보를 가져와야 함
                "empName", userDTO.getEmpName(),
                "roles", roles,
                "perms", perms,
                "type", "access"
        );

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expiryDate = now.plusMinutes(ACCESS_EXP_TIME);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(expiryDate.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh Token을 생성합니다.
     */
    public String createRefreshToken(UserDTO userDTO) { // ★ 시그니처 수정 및 이름 통일 (선택적)
        // Refresh Token은 최소한의 정보만 담습니다.
        Map<String, Object> claims = Map.of(
                "userId", userDTO.getUserId(),
                "username", userDTO.getUsername(),
                "type", "refresh"
        );

        ZonedDateTime now = ZonedDateTime.now();
        // Refresh Token 유효 시간 적용 (일)
        ZonedDateTime expiryDate = now.plusDays(REFRESH_EXP_TIME_DAYS);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(expiryDate.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // --- 2. 토큰 검증 및 정보 추출 메서드 (변경 없음) ---

    /**
     * 주어진 토큰이 유효한지 검증하고 Claims(Payload)를 반환합니다.
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            // 모든 JWT 관련 예외를 잡아 JwtException으로 통일하여 던지거나, 로그 기록 후 특정 오류 코드를 반환합니다.
            log.warn("JWT 검증 실패: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            throw e; // Custom Exception으로 래핑하여 던지는 것이 일반적입니다. (여기서는 JwtException을 그대로 던집니다.)
        }
    }
}