package com.zuu.zojbackendgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/31 12:01
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, RedisAutoConfiguration.class})
public class ZojBackendGateway {
    public static void main(String[] args) {
        SpringApplication.run(ZojBackendGateway.class);
    }
}
