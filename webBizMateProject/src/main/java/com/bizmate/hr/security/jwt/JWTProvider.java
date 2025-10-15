package com.bizmate.hr.security.jwt;

import com.bizmate.hr.security.UserPrincipal;
import io.jsonwebtoken.*;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

import java.util.stream.Collectors;

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
    private static final Key ks = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    private final long accessTokenValidityMillis = 1000L * 60 * 60;    // 1시간
    private final long refreshTokenValidityMillis = 1000L * 60 * 60 * 24 * 7; // 7일
    // ★★★ --------------------- ★★★

    // --- 1. 토큰 생성 메서드 ---

    /**
     * Access Token을 생성합니다.
     */
    public String createAccessToken(UserPrincipal principal) {
        log.info("jwt생성 직전 권한 목록 : {}",principal.getAuthorities());
        return createToken(principal, accessTokenValidityMillis);
    }

    /**
     * Refresh Token을 생성합니다.
     */
    // --- Refresh Token 생성 (재발급용) ---
    public String createRefreshToken(UserPrincipal principal) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", principal.getUserId());
        claims.put("type", "refresh");

        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidityMillis);

        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(ks, SignatureAlgorithm.HS256 )
                .compact();
    }

    private String createToken(UserPrincipal principal, long validityMillis) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", principal.getUserId());
        claims.put("empId",principal.getEmpId());
        claims.put("roles", principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("type", "access");

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityMillis);

        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(ks,SignatureAlgorithm.HS256)
                .compact();
    }

    // --- 2. 토큰 검증 및 정보 추출 메서드 (변경 없음) ---

    /**
     * 주어진 토큰이 유효한지 검증하고 Claims(Payload)를 반환합니다.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(ks).parseClaimsJws(token);
            log.debug("✅ 토큰 검증 성공");
            return true;
        } catch (JwtException e) {
            log.warn("❌ 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    public Claims parseClaims(String token) {

        return Jwts.parser().setSigningKey(ks).parseClaimsJws(token).getBody();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        String username = claims.getSubject();
        Long userId = claims.get("uid", Long.class);
        Long empId = claims.get("empId", Long.class);

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.getOrDefault("roles", Collections.emptyList());

        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserPrincipal principal = new UserPrincipal(
                userId,
                empId,
                username,
                "",  // 비밀번호는 JWT 안에 없음
                true,
                false,
                authorities
        );

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }
}
