package com.bizmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.bizmate.hr.HrJpaConfig;
import com.bizmate.groupware.GroupwareJpaConfig;
import com.bizmate.common.CommonConfig;

@SpringBootApplication(scanBasePackages = {
        "com.bizmate.config",     // ✅ SecurityConfig, JwtTokenProvider 등 포함
        "com.bizmate.common"      // ✅ 공통 계층
})
@Import({ HrJpaConfig.class, GroupwareJpaConfig.class, CommonConfig.class })
public class WebBizMateProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebBizMateProjectApplication.class, args);
	}

}
