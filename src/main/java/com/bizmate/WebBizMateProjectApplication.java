package com.bizmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import com.bizmate.common.CommonConfig;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.bizmate"})
@EnableJpaRepositories(basePackages = {"com.bizmate"})
@ComponentScan(basePackages = {"com.bizmate"})
public class WebBizMateProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebBizMateProjectApplication.class, args);
	}

}
