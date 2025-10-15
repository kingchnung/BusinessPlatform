package com.bizmate.common.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
<<<<<<< HEAD
import org.springframework.context.annotation.Primary;

@Configuration("projectRootConfig")
public class RootConfig {
    @Bean
    @Primary
    ModelMapper getMapper() {
=======

@Configuration
public class RootConfig {
    @Bean
    ModelMapper salesGetMapper() {
>>>>>>> 7e631613e802f528445a8f222c1ec078e9c8bda3
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true)    // 필드 매칭 활성화
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)      // 접근 수준 설정
                .setMatchingStrategy(MatchingStrategies.LOOSE);     // 매칭 전략 느슨

<<<<<<< HEAD

=======
>>>>>>> 7e631613e802f528445a8f222c1ec078e9c8bda3
        return modelMapper;
    }
}
