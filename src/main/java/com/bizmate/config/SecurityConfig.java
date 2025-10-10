package com.bizmate.config;

import com.bizmate.hr.security.filter.JWTCheckFilter;
import com.bizmate.hr.security.handler.APILoginFailHandler;
import com.bizmate.hr.security.handler.CustomAccessDeniedHandler;
import com.bizmate.hr.security.handler.CustomAuthenticationEntryPoint;
import com.bizmate.hr.security.jwt.JWTProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JWTProvider jwtProvider;
    private final APILoginFailHandler apiLoginFailHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * ✅ PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ✅ JWT 인증 필터 (토큰 유효성 + SecurityContext 등록)
     */
    @Bean
    public JWTCheckFilter jwtCheckFilter() {
        return new JWTCheckFilter(jwtProvider);
    }

    /**
     * ✅ SecurityFilterChain - 통합 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("▶▶▶ SecurityConfig: 필터 체인 초기화 시작");

        return http
                // 1️⃣ 기본 인증 관련 기능 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 2️⃣ CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3️⃣ 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/member/login",   // 로그인 엔드포인트
                                "/api/mock/login",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/error"
                        ).permitAll() // 인증 불필요
                        .requestMatchers("/api/**").authenticated() // JWT 인증 필요
                        .anyRequest().permitAll()
                )

                // 4️⃣ JWT 검증 필터 등록 (UsernamePasswordAuthenticationFilter 전에 실행)
                .addFilterBefore(jwtCheckFilter(), UsernamePasswordAuthenticationFilter.class)

                // 5️⃣ 인증 및 인가 예외 처리
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(customAccessDeniedHandler)
                )

                .build();
    }

    /**
     * ✅ CORS 설정 (React와 통신 허용)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*")); // React Localhost 등 모든 Origin 허용
        configuration.setAllowCredentials(true); // 쿠키 / 인증정보 허용
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization")); // JWT 토큰 헤더 노출 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * ✅ 권한 계층 설정 (상위 권한 → 하위 권한 자동 상속)
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = """
            ROLE_CEO > sys:admin
            sys:admin > sys:manage
            sys:manage > data:write:all
            sys:manage > data:read:all
            ROLE_MANAGER > ROLE_EMPLOYEE
            data:read:all > data:read:self
            data:read:all > emp:read
            data:read:all > dept:read
            data:read:all > pos:read
            data:read:all > grade:read
            data:write:all > data:write:self
            """;
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }
}
