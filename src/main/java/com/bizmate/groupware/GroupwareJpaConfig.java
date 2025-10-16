package com.bizmate.groupware;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;

@Configuration
@EntityScan(basePackages = {
        "com.bizmate.common.domain",
        "com.bizmate.groupware.approval.domain",
        "com.bizmate.groupware.board.domain",
        "com.bizmate.hr.domain"                     // ✅ hr 엔티티 명시
})
@EnableJpaRepositories(basePackages = {
        "com.bizmate.groupware.approval.repository",
        "com.bizmate.groupware.board.repository",
        "com.bizmate.hr.repository"                 // ✅ hr 리포지토리 추가
})
@ComponentScan(basePackages = {
        "com.bizmate.groupware.approval.service",
        "com.bizmate.groupware.approval.api",
        "com.bizmate.groupware.board.service",
        "com.bizmate.groupware.board.api",
        "com.bizmate.hr"                            // ✅ hr 서비스/설정까지 포함 시 안전
})
public class GroupwareJpaConfig {
    // 전자결재 전용 + HR repository 접근 가능
}