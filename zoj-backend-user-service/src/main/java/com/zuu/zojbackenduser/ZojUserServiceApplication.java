package com.zuu.zojbackenduser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/29 11:47
 */
@SpringBootApplication
@MapperScan("com.zuu.zojbackenduser.mapper")
public class ZojUserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZojUserServiceApplication.class);
    }
}
