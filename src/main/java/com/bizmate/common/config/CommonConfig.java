package com.bizmate.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

@Configuration // ✅ BaseEntity의 @CreatedDate, @LastModifiedDate 활성화
@ComponentScan(basePackages = {
        "com.bizmate.common.exception",
        "com.bizmate.common.logging",
        "com.bizmate.common.service"
})
public class CommonConfig {
    // ✅ 공통 Bean 등록 (AOP, 예외, 로깅, 외부 검증 등)
    // ⚙️ HR / Groupware 둘 다 사용 가능
}