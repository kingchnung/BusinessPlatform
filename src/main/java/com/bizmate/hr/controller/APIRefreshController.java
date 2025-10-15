package com.bizmate.hr.controller;

import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.security.jwt.JWTProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * [APIRefreshController]
 * - Access Token이 만료되었을 때 Refresh Token을 이용하여 토큰을 재발급하는 엔드포인트
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class APIRefreshController {

    private final JWTProvider jwtProvider;

    // Refresh Token 갱신 임계값: 만료일이 3일 미만으로 남았을 경우 Refresh Token도 재발급
    private final long REFRESH_TOKEN_ROTATION_THRESHOLD_DAYS = 1;

    @RequestMapping(value = "/api/member/refresh", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> refresh(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("refreshToken") String refreshToken) {

        // 1. Access Token 추출 및 유효성 확인
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Refresh 요청 실패: Bearer 헤더 형식이 올바르지 않음.");
            throw new JwtException("Bearer 헤더 형식이 올바르지 않습니다.");
        }
        String accessToken = authHeader.substring(7);

        // 2. Access Token 만료 여부 확인 (만료되지 않았다면 재발급 거부)
        if (!checkExpiredToken(accessToken)) {
            log.warn("Access Token이 아직 유효합니다. 토큰 재발급을 거부하고 기존 토큰을 반환합니다.");
            // 학원 예제와 동일하게 기존 토큰 반환
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        // 3. Refresh Token 검증 및 Claims 추출
        // 만료, 서명 오류 등 모든 JWT 예외는 여기서 처리됩니다.
        Claims claims = jwtProvider.validateToken(refreshToken);

        // 4. Claims를 이용하여 UserDTO 재구성
        UserDTO userDTO = createUserDTOFromClaims(claims);

        // 5. 새 Access Token 발급
        String newAccessToken = jwtProvider.createAccessToken(
                userDTO,
                userDTO.getRoleNames(),
                userDTO.getPermissionNames());

        // 6. Refresh Token 갱신 여부 결정 (Rotation)
        String newRefreshToken = refreshToken;
        Date expirationDate = claims.getExpiration();

        if (checkTimeForRotation(expirationDate)) {
            // 갱신 임계값 미만 남음 -> 새 Refresh Token 발급
            newRefreshToken = jwtProvider.createRefreshToken(userDTO);
            log.info("Refresh Token 만료일이 임박하여 새 Refresh Token을 발급했습니다.");
        } else {
            log.info("Refresh Token이 충분히 유효하여 기존 토큰을 유지합니다.");
        }

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    /**
     * Access Token의 만료 여부만 확인합니다.
     * @return true: 만료됨, false: 만료되지 않음
     */
    private boolean checkExpiredToken(String accessToken) {
        try {
            jwtProvider.validateToken(accessToken);
            return false;
        } catch (ExpiredJwtException e) {
            return true; // ExpiredJwtException 발생 -> 만료됨
        } catch (JwtException e) {
            // 다른 JWT 오류는 여기서 처리하지 않고, Refresh Token 검증 단계에서 예외를 던지게 합니다.
            log.error("Access Token 검증 중 만료 외의 오류 발생: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Refresh Token 만료일이 갱신 임계값(3일) 이내인지 확인합니다.
     * 학원 예제의 'checkTime' 함수와 동일한 목적입니다.
     */
    private boolean checkTimeForRotation(Date expirationDate) {
        long remainingMillis = expirationDate.getTime() - System.currentTimeMillis();
        long rotationThresholdMillis = REFRESH_TOKEN_ROTATION_THRESHOLD_DAYS * 24 * 60 * 60 * 1000;

        return remainingMillis < rotationThresholdMillis;
    }

    /**
     * Claims 정보를 기반으로 UserDTO를 재구성합니다.
     * JWTCheckFilter의 로직과 동일하게 권한 정보를 복원합니다.
     */
    private UserDTO createUserDTOFromClaims(Claims claims) {
        Long userId = claims.get("userId", Long.class);
        Long empId = claims.get("empId", Long.class);
        String username = claims.get("username", String.class);
        String empName = claims.get("empName", String.class);
        String departmentCode = claims.get("departmentCode", String.class);

        List<String> roleNames = (List<String>) claims.get("roles");
        List<String> permissionNames = (List<String>) claims.get("perms");

        // Authorities 컬렉션 생성 (JWTCheckFilter와 동일한 헬퍼 메서드 사용)
        Collection<? extends GrantedAuthority> authorities = createAuthorities(roleNames, permissionNames);

        return new UserDTO(
                userId,
                empId,
                departmentCode,
                username,
                "NOPASSWORD",
                empName,
                true,
                roleNames,
                permissionNames,
                authorities
        );
    }

    /**
     * JWTCheckFilter에서 사용한 Authorities 생성 로직과 일치시킵니다.
     */
    private Collection<? extends GrantedAuthority> createAuthorities(
            List<String> roleNames, List<String> permissionNames) {

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if (roleNames != null) {
            roleNames.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .forEach(authorities::add);
        }

        if (permissionNames != null) {
            permissionNames.stream()
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }

        return authorities;
    }
}