package com.zuu.zojbackendquestion;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/29 11:47
 */
@SpringBootApplication
@MapperScan("com.zuu.zojbackendquestion.mapper")
@EnableFeignClients(basePackages = "com.zuu.zojbackendapi.client")
public class ZojQuestionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZojQuestionServiceApplication.class);
    }
}
