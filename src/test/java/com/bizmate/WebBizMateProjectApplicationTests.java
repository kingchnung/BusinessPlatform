package com.bizmate;

import com.bizmate.config.QuerydslConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

@Import(QuerydslConfig.class)
@SpringBootTest
class WebBizMateProjectApplicationTests {

    @Autowired
    private ApplicationContext context;

	@Test
	void contextLoads() {
        Arrays.stream(context.getBeanDefinitionNames())
                .forEach(System.out::println);
	}

}
