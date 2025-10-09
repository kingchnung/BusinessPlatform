package com.bizmate.hr;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;

@Configuration
// ✅ HR 도메인 + 공통 도메인만 스캔
@EntityScan(basePackages = {
        "com.bizmate.common.domain",   // BaseEntity 등 공통 엔티티
        "com.bizmate.hr"               // Employees, Departments, UserRoles 등
})
@EnableJpaRepositories(basePackages = {
        "com.bizmate.hr.repository"    // HR용 Repository
})
@ComponentScan(basePackages = {
        "com.bizmate.hr.service",      // HR 비즈니스 로직
        "com.bizmate.hr.controller"    // HR 전용 컨트롤러 (있다면)
})
public class HrJpaConfig {
    // ✅ HR 모듈 전용 설정
    // ⚙️ Security, JWT, Groupware 관련 Bean은 포함하지 않음
}