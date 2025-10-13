package com.bizmate.hr.config;

import com.bizmate.hr.security.filter.JWTCheckFilter;
import com.bizmate.hr.security.handler.APILoginFailHandler;
import com.bizmate.hr.security.handler.APILoginSuccessHandler;
import com.bizmate.hr.security.handler.CustomAccessDeniedHandler;
import com.bizmate.hr.security.jwt.JWTProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@EnableMethodSecurity // @PreAuthorize, @PostAuthorize 등을 활성화
@Slf4j
public class CustomSecurityConfig {

    // 필요한 Bean들을 주입받습니다.
    private final JWTProvider jwtProvider;
    private final APILoginFailHandler apiLoginFailHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    // ★★★ PasswordEncoder Bean 등록 ★★★
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // --- 1. Security Filter Chain 설정 ---

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("▶▶▶ CustomSecurityConfig FilterChain 설정 시작");

        // 1. CORS 설정: 모든 출처 허용 (개발 환경 기준)
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 2. CSRF 비활성화: API 서버는 세션을 사용하지 않으므로 CSRF를 비활성화합니다. (학원 예제와 동일)
        http.csrf(csrf -> csrf.disable());

        // 3. 세션 관리: STATELESS 설정 (JWT 기반 무상태 서버) (학원 예제와 동일)
        http.sessionManagement(s ->
                s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 4. Form 로그인 설정: API 서버 로그인 URL 및 핸들러 설정
        http.formLogin(config -> {
            config.loginPage("/api/member/login"); // 로그인 엔드포인트 지정 (실제 로그인 처리는 Spring이 수행)
            // JWTProvider를 인수로 전달하여 SuccessHandler Bean 생성
            config.successHandler(new APILoginSuccessHandler(jwtProvider));
            // 주입받은 FailHandler Bean 사용
            config.failureHandler(apiLoginFailHandler);
        });

        // 5. JWT 필터 추가: UsernamePasswordAuthenticationFilter 이전에 JWTCheckFilter를 실행
        // JWTProvider를 인수로 전달하여 필터 Bean 생성
        http.addFilterBefore(
                new JWTCheckFilter(jwtProvider),
                UsernamePasswordAuthenticationFilter.class
        );

        // 6. 예외 처리 핸들러 설정
        http.exceptionHandling(e -> {
            // 권한 부족 예외 (403 Forbidden) 처리
            e.accessDeniedHandler(customAccessDeniedHandler);
            // 인증되지 않은 접근 (401 Unauthorized) 처리를 위한 EntryPoint는
            // JWTCheckFilter에서 토큰 오류 시 직접 처리하거나, 별도 Filter를 통해 처리할 수 있습니다.
            // 여기서는 학원 예제 구조를 따라 AccessDeniedHandler만 명시합니다.
        });

        // 7. 로그아웃 설정: API 서버에서는 클라이언트가 토큰을 삭제하므로, 서버의 로그아웃 로직은 단순하게 설정
        http.logout(logout -> logout.logoutUrl("/api/member/logout").permitAll());

        return http.build();
    }

    // --- 2. CORS 설정 Bean ---

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        log.info("▶▶▶ CustomSecurityConfig CORS 설정");

        CorsConfiguration configuration = new CorsConfiguration();

        // 모든 출처에서 접근 허용 (개발 환경)
        configuration.setAllowedOriginPatterns(List.of("*"));
        // 모든 메서드 허용
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 자격 증명(쿠키, Authorization 헤더 등) 허용
        configuration.setAllowCredentials(true);
        // 모든 헤더 허용
        configuration.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 URL 패턴에 대해 위 설정을 적용
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}