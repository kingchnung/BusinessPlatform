package com.bizmate.hr.config;

import com.bizmate.hr.security.filter.JWTCheckFilter;
import com.bizmate.hr.security.handler.CustomAccessDeniedHandler;
import com.bizmate.hr.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class CustomSecurityConfig {

    private final JWTCheckFilter jwtCheckFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * PasswordEncoder 등록 (비밀번호 해싱)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Spring Security 필터 체인
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("▶▶▶ CustomSecurityConfig FilterChain 설정 시작");

        http
                // 1️⃣ 기본 보안 기능 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2️⃣ 세션 사용 안 함 (JWT 기반)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3️⃣ 인가 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 경로들
                        .requestMatchers(
                                "/api/auth/**",       // 로그인, 회원가입
                                "/api/member/refresh",// 토큰 재발급
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/h2-console/**"
                        ).permitAll()

                        // OPTIONS 메서드 허용 (CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // 4️⃣ JWT 필터 등록
                .addFilterBefore(jwtCheckFilter, UsernamePasswordAuthenticationFilter.class)

                // 5️⃣ 예외 처리 (JWT 인증 예외 대응)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint) // 인증 실패 (401)
                        .accessDeniedHandler(accessDeniedHandler)           // 권한 부족 (403)
                );

        return http.build();
    }

    /**
     * CORS 설정 (React 등 클라이언트와 통신 허용)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("▶▶▶ CustomSecurityConfig CORS 설정");

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * RoleHierarchy 설정 (기존 유지)
     * - JWT 기반이라도, @PreAuthorize("hasRole('ROLE_MANAGER')") 같은 곳에서 역할 계층이 적용됨
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = """
            # 1. 최상위 역할
            ROLE_CEO > sys:admin

            # 2. 시스템 관리 권한 계층
            sys:admin > sys:manage
            sys:manage > data:write:all
            sys:manage > data:read:all

            # 3. 일반 역할
            ROLE_MANAGER > ROLE_EMPLOYEE

            # 4. 읽기 권한 계층
            data:read:all > data:read:self
            data:read:all > emp:read
            data:read:all > dept:read
            data:read:all > pos:read
            data:read:all > grade:read

            # 5. 쓰기 권한 계층
            data:write:all > data:write:self
            """;
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }
}
