package com.bizmate.project.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration("projectRootConfig")
public class RootConfig {
    @Bean
    @Primary
    ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setFieldMatchingEnabled(true)                             // 필드 매칭을 활성화
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)   // 필드 접근 수준을 PRIVATE으로 설정
                .setMatchingStrategy(MatchingStrategies.LOOSE);                                 // 매칭 전략을 느슨하게 설정



        return modelMapper;
    }

}