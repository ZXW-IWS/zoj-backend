package com.zuu.codesandbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/29 19:33
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, RedisAutoConfiguration.class})
public class ZojCodeSandBoxApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZojCodeSandBoxApplication.class);
    }
}
