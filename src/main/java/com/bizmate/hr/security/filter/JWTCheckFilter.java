package com.bizmate.hr.security.filter;

import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.security.jwt.JWTProvider;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;


/**
 * [JWTCheckFilter]
 * - 모든 요청에 앞서 Access Token의 유효성을 검증하고, 유효할 경우 SecurityContext에 인증 정보를 설정
 */
@Slf4j
@RequiredArgsConstructor
public class JWTCheckFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider; // ★ 변경점 1: JWTProvider 주입

    // 필터 제외 URL 패턴 (학원 예제의 shouldNotFilter 로직 대체)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // OPTIONS 메서드와 로그인/리프레시 요청은 필터를 건너뜁니다.
        String path = request.getRequestURI();
        return request.getMethod().equals("OPTIONS") ||
                path.startsWith("/api/member/login") ||
                path.startsWith("/api/member/refresh");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.length() < 7 || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = authHeader.substring(7);
            Claims claims = jwtProvider.validateToken(accessToken);

            String username = claims.get("username", String.class);
            String empName = claims.get("empName", String.class);
            String departmentCode = claims.get("departmentCode", String.class);
            Long userId = claims.get("userId", Long.class);
            Long empId = claims.get("empId", Long.class);

            List<String> roleNames = (List<String>) claims.get("roles");
            List<String> permissionNames = (List<String>) claims.get("perms");

            // ★★★ 최종 수정 로직: Authorities 컬렉션 생성 ★★★
            Collection<? extends GrantedAuthority> authorities = createAuthorities(roleNames, permissionNames);

            // 4. UserDTO 객체 재구성
            UserDTO userDTO = new UserDTO(
                    userId,
                    empId,
                    departmentCode,
                    username,
                    "NOPASSWORD",
                    empName,
                    true,
                    roleNames,
                    permissionNames,
                    authorities // ★★★ 생성된 authorities 컬렉션을 마지막 인수로 전달 ★★★
            );

            // 5. SecurityContext에 인증 정보 설정
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDTO, null, userDTO.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);


        } catch (ExpiredJwtException e) {
            log.warn("❌ JWT 만료: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            new Gson().toJson(Map.of("error", "ERROR_EXPIRED_TOKEN", "message", e.getMessage()), response.getWriter());
            return;

        } catch (JwtException e) {
            log.warn("❌ JWT 유효성 실패: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            new Gson().toJson(Map.of("error", "ERROR_INVALID_TOKEN", "message", e.getMessage()), response.getWriter());
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * JWT 클레임에서 추출한 역할 및 권한 목록으로 Spring Security Authorities 컬렉션을 생성합니다.
     */
    private Collection<? extends GrantedAuthority> createAuthorities(
            List<String> roleNames, List<String> permissionNames) {

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 역할(Role) 등록: "ROLE_" 접두어 사용
        if (roleNames != null) {
            roleNames.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .forEach(authorities::add);
        }

        // 권한(Permission) 등록: 세부 권한 체크에 사용
        if (permissionNames != null) {
            permissionNames.stream()
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }

        return authorities;
    }
}