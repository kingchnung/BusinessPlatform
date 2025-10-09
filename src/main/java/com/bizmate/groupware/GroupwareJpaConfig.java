package com.bizmate.groupware;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;

@Configuration
// ✅ 전자결재 도메인 + 공통 도메인만 스캔
@EntityScan(basePackages = {
        "com.bizmate.common.domain",                // BaseEntity 등 공통 엔티티
        "com.bizmate.groupware.approval.domain",     // 전자결재 엔티티
        "com.bizmate.hr"
})
@EnableJpaRepositories(basePackages = {
        "com.bizmate.groupware.approval.repository" // 전자결재 Repository
})
@ComponentScan(basePackages = {
        "com.bizmate.groupware.approval.service",   // 결재 서비스 로직
        "com.bizmate.groupware.approval.api"        // 결재 컨트롤러
})
public class GroupwareJpaConfig {
    // ✅ 전자결재 전용 설정
    // ⚠️ HR 영역 클래스는 스캔하지 않음 (중복 방지)
}