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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

/**
 * [JWTCheckFilter]
 * - 모든 요청에서 JWT Access Token을 확인하고,
 * 유효하면 SecurityContext에 Authentication을 설정하는 역할.
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

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7); // “Bearer “ 제거

        try {
            if (jwtProvider.validateToken(token)) {
                Authentication authentication = jwtProvider.getAuthentication(token);

                if (authentication instanceof org.springframework.security.authentication.UsernamePasswordAuthenticationToken authToken) {
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("JWT 인증 성공: {}", authToken.getName());
                }
            }
        } catch (ExpiredJwtException e) {
            log.warn("JWT 만료됨: {}", e.getMessage());
            // Access Token 만료 → RefreshController에서 처리
        } catch (JwtException e) {
            log.error("JWT 검증 실패: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT 필터 처리 중 예외 발생", e);
        }

        filterChain.doFilter(request, response);
    }
}
