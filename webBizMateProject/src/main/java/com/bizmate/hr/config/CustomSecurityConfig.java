package com.bizmate.hr.config;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    public JWTCheckFilter jwtCheckFilter() {
        // 주입받은 jwtProvider를 사용하여 필터 인스턴스를 생성합니다.
        return new JWTCheckFilter(jwtProvider);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("▶▶▶ CustomSecurityConfig FilterChain 설정 시작");

                // 1. 세션 기반 인증/폼 로그인/CSRF/HTTP Basic 모두 비활성화
        return http
                .csrf(AbstractHttpConfigurer::disable)         // CSRF 보호 비활성화 (API 서버이므로)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(AbstractHttpConfigurer::disable)      // HTTP Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable)      // 폼 로그인 비활성화 (HTML 로그인 페이지 방지)

                // 2. 세션 관리를 STATELESS로 설정 (JWT 토큰으로만 인증하겠다는 의미)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 인가 규칙 설정 (어떤 경로에 인증이 필요한지 정의)

                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 허용 엔드포인트
                        .requestMatchers("/api/member/login", "/h2-console/**").permitAll() // 로그인 경로는 permitAll

                        // 그 외 모든 /api/ 요청에 대해 인증(토큰) 필요
                        .requestMatchers("/api/**").authenticated()

                        // 나머지 요청 허용 (선택적)
                        .anyRequest().permitAll()
                )

                // 4. JWT 검증 필터 추가 (Bean 메서드 호출)
                // jwtCheckFilter()를 호출하여 Bean 인스턴스를 가져옵니다.
                .addFilterBefore(jwtCheckFilter(), UsernamePasswordAuthenticationFilter.class)

                // 5. 예외 처리 (인증 실패 시 401 응답을 반환하도록 설정)
                // .exceptionHandling()은 체이닝의 일부이므로 http.exceptionHandling() 대신 .exceptionHandling()으로 시작
                .exceptionHandling(handler -> handler
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()))

                // 6. 모든 설정이 끝났으므로 .build()를 호출하고 그 결과를 return 합니다.
                .build(); // ★ 수정 완료: return http.build();가 아닌 return http.build();
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
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();

        // 권한 계층 정의 문자열
        String hierarchy = """
            # 1. 최상위 역할 (CEO는 모든 권한을 가집니다)
            ROLE_CEO > sys:admin
            
            # 2. 시스템 관리 권한 계층
            sys:admin > sys:manage         # sys:admin은 sys:manage를 포함
            sys:manage > data:write:all    # 시스템 관리 권한은 모든 쓰기 권한을 포함
            sys:manage > data:read:all     # 시스템 관리 권한은 모든 읽기 권한을 포함
            
            # 3. 역할 계층 (Manager는 Employee를 포함)
            ROLE_MANAGER > ROLE_EMPLOYEE
            
            # 4. 데이터 권한 상속 관계 (data:read:all이 모든 조회 권한을 포함)
            data:read:all > data:read:self
            data:read:all > emp:read
            data:read:all > dept:read
            data:read:all > pos:read
            data:read:all > grade:read
            
            # 5. 기타 개별 쓰기 권한
            data:write:all > data:write:self
            """;

        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

}