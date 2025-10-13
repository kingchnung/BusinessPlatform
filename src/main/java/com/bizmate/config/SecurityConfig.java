package com.bizmate.config;

import com.bizmate.hr.security.filter.JWTCheckFilter;
import com.bizmate.hr.security.handler.*;
import com.bizmate.hr.security.jwt.JWTProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JWTProvider jwtProvider;
    private final APILoginSuccessHandler apiLoginSuccessHandler;
    private final APILoginFailHandler apiLoginFailHandler;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    /**
     * ✅ 비밀번호 인코더 Bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ✅ JWT 필터
     */
    @Bean
    public JWTCheckFilter jwtCheckFilter() {
        return new JWTCheckFilter(jwtProvider);
    }

    /**
     * ✅ Security 필터 체인 (통합형)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("▶▶▶ [SecurityConfig] 필터 체인 초기화");

        http
                // 기본 인증 비활성화
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 접근 제어
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/h2-console/**",
                                "/error"
                        ).permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )

                // JWT 필터
                .addFilterBefore(jwtCheckFilter(), UsernamePasswordAuthenticationFilter.class)

                // 로그인/로그아웃 설정
                .formLogin(form -> form
                        .loginPage("/api/member/login")
                        .successHandler(apiLoginSuccessHandler)
                        .failureHandler(apiLoginFailHandler)
                )
                .logout(logout -> logout.logoutUrl("/api/member/logout").permitAll())

                // 예외 처리
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }

    /**
     * ✅ CORS 설정 (React 등 외부 클라이언트 허용)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * ✅ 권한 계층 설정 (ROLE 상속)
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("""
            ROLE_CEO > sys:admin
            sys:admin > sys:manage
            sys:manage > data:write:all
            sys:manage > data:read:all
            ROLE_MANAGER > ROLE_EMPLOYEE
            data:read:all > emp:read
            data:read:all > dept:read
            data:read:all > pos:read
            data:read:all > grade:read
            data:write:all > emp:create
            data:write:all > emp:update
            data:write:all > emp:delete
        """);
        return hierarchy;
    }

    // ✅ AuthenticationManager Bean 등록 (AuthService에서 주입 가능)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
