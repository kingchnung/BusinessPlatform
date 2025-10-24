package com.bizmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "com.bizmate")
public class WebBizMateProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebBizMateProjectApplication.class, args);
	}

}
