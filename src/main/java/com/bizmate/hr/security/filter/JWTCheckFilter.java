package com.bizmate.hr.security.filter;

import com.bizmate.hr.security.UserPrincipal;
import com.bizmate.hr.security.jwt.JWTProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

/**
 * [JWTCheckFilter]
 * - 모든 요청에서 JWT Access Token을 확인하고,
 *   유효하면 SecurityContext에 Authentication을 설정하는 역할.
 * - UserPrincipal 기반으로 변경됨.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JWTCheckFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("🧭 JWTCheckFilter 요청 URI: {}", request.getRequestURI());
        String header = request.getHeader("Authorization");
        String path = request.getRequestURI();

        // ✅ 업로드 미리보기 / 다운로드는 인증 없이 통과
        if (path.startsWith("/api/attachments/preview") || path.startsWith("/api/attachments/download")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("▶▶▶ JWTCheckFilter 실행... 요청 URI: {}", request.getRequestURI());

        if (header == null || !header.startsWith("Bearer ")) {
            log.warn("▶▶▶ JWT 토큰 없음 또는 Bearer 타입 아님. 다음 필터로 넘어갑니다.");
            filterChain.doFilter(request, response);
            return;
        }


        String token = header.substring(7); // “Bearer “ 제거

        try {
            String token = header.substring(7); // "Bearer " 제거

            // ✅✅✅ 핵심 수정: validateToken()을 호출하지 않고, 바로 getAuthentication()을 시도합니다.
            // getAuthentication 내부에서 토큰 파싱/검증이 실패하면 예외가 발생하여 catch 블록으로 넘어갑니다.
            Authentication authentication = jwtProvider.getAuthentication(token);

            // SecurityContext에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("✅ JWT 인증 성공 - 사용자: {}", authentication.getName());

        } catch (Exception e) {
            // JWTProvider에서 발생하는 모든 예외(만료, 서명오류 등)를 여기서 잡습니다.
            log.error("❌ JWT 토큰 처리 중 오류 발생: {}", e.getMessage());
            // SecurityContext를 비워 확실하게 인증되지 않았음을 보장합니다.
            SecurityContextHolder.clearContext();
        }

        // 다음 필터 체인을 계속 진행합니다.
        filterChain.doFilter(request, response);
    }
}
