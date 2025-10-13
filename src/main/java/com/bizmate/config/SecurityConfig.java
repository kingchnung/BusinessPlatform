package com.bizmate.config;

import com.bizmate.hr.security.filter.JWTCheckFilter;
import com.bizmate.hr.security.handler.APILoginFailHandler;
import com.bizmate.hr.security.handler.APILoginSuccessHandler;
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
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * ✅ 비밀번호 암호화 Bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ✅ JWT 필터 Bean
     */
    @Bean
    public JWTCheckFilter jwtCheckFilter() {
        return new JWTCheckFilter(jwtProvider);
    }

    /**
     * ✅ 보안 필터 체인 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("▶▶▶ SecurityConfig: 필터 체인 초기화 시작");

        http
                // 1️⃣ 기본 인증 기능 비활성화
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 2️⃣ CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3️⃣ 요청 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/member/login",
                                "/api/mock/login",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/error"
                        ).permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )

                // 4️⃣ 로그인/로그아웃 설정
                .formLogin(form -> form
                        .loginPage("/api/member/login")
                        .successHandler(apiLoginSuccessHandler)
                        .failureHandler(apiLoginFailHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/member/logout")
                        .permitAll()
                )

                // 5️⃣ JWT 필터 등록
                .addFilterBefore(jwtCheckFilter(), UsernamePasswordAuthenticationFilter.class)

                // 6️⃣ 예외 처리
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(customAccessDeniedHandler)
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
     * ✅ 권한 계층 설정 (상위 권한 자동 상속)
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
            """);
        return hierarchy;
    }
}
