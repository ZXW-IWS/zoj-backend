package com.zuu.zojbackendapi.client;

import com.zuu.domain.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/29 19:53
 */
@FeignClient(name = "user-service",path = "/api/user/inner")
public interface UserFeignClient {
    @GetMapping("/id")
    Long getIdByToken(@RequestParam("token") String token);
    @GetMapping("/isadmin")
    Boolean isAdmin(@RequestParam("uid") Long uid);
    @GetMapping("/user")
    UserDto getUserById(@RequestParam("uid") Long uid);
}
