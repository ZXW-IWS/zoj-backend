package com.zuu.zojbackendjudge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/29 12:11
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.zuu.zojbackendapi.client")
public class ZojJudgeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZojJudgeServiceApplication.class);
    }
}
