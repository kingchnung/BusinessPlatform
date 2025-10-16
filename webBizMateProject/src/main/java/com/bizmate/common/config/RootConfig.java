package com.bizmate.common.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RootConfig {
    @Bean
    ModelMapper getMapper() {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true)    // 필드 매칭 활성화
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)      // 접근 수준 설정
                .setMatchingStrategy(MatchingStrategies.LOOSE);     // 매칭 전략 느슨

        return modelMapper;
    }
}
