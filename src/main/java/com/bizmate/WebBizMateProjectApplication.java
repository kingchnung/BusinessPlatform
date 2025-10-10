package com.bizmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.bizmate.groupware.GroupwareJpaConfig;
import com.bizmate.common.CommonConfig;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@Import({ GroupwareJpaConfig.class, CommonConfig.class })
public class WebBizMateProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebBizMateProjectApplication.class, args);
	}

}
