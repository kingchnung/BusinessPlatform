package com.bizmate.hr.security.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.google.gson.Gson;
import com.bizmate.hr.dto.user.UserDTO; // 저희가 정의한 UserDTO 사용
import com.bizmate.hr.security.jwt.JWTProvider; // 이전 단계에서 설계한 JWTProvider 사용
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * [APILoginSuccessHandler]
 * - 로그인 성공 후 JWT를 생성하여 JSON 형태로 응답하는 핸들러
 */
@Slf4j
@RequiredArgsConstructor // ★ 변경점 1: JWTProvider 주입을 위해 Lombok의 RequiredArgsConstructor 사용
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {

    // ★ 변경점 1: JWTProvider를 필드로 주입받음
    private final JWTProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info("▶▶▶ APILoginSuccessHandler 실행: 로그인 성공");

        // ★ 변경점 2: DTO 형 변환 (MemberDTO -> UserDTO)
        UserDTO userDTO = (UserDTO) authentication.getPrincipal();

        // JWT에 담길 클레임 데이터 (userId, username, empName, roles, perms 포함)
        Map<String, Object> claims = userDTO.getClaims();

        // ★ 변경점 3: JWTProvider를 사용하여 토큰 생성 (JWTUtil.generateToken 대체)
        String accessToken = jwtProvider.generateAccessToken(userDTO);
        String refreshToken = jwtProvider.generateRefreshToken(userDTO);

        claims.put("accessToken", accessToken);
        claims.put("refreshToken", refreshToken);

        // 응답 설정 (학원 예제와 동일)
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter printWriter = response.getWriter();

        // Gson을 사용하여 claims Map을 JSON 문자열로 변환 후 전송
        new Gson().toJson(claims, printWriter);
        printWriter.close();

        log.info("JWT 발급 완료: Access Token 및 Refresh Token 응답 전송");
    }
}