package com.zuu.zojbackendcommon.config;

import com.zuu.zojbackendcommon.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author zuu
 * @Description
 * @Date 2024/11/4 12:55
 */
@Configuration
//@ComponentScan(basePackages = "com.zuu.zojbackendcommon.exception")
public class ComponentConfig {
    @Bean
    public GlobalExceptionHandler globalExceptionHandler(){
        return new GlobalExceptionHandler();
    }
}
