package com.zuu.zojbackenduser.controller.inner;

import com.zuu.domain.dto.UserDto;
import com.zuu.zojbackenduser.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/29 20:02
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController{
    @Resource
    UserService userService;
    @GetMapping("/id")
    Long getIdByToken(@RequestParam("token") String token){
        return userService.getIdByToken(token);
    }
    @GetMapping("/isadmin")
    Boolean isAdmin(@RequestParam("uid") Long uid){
        return userService.isAdmin(uid);
    }
    @GetMapping("/user")
    UserDto getUserById(@RequestParam("uid") Long uid){
        return userService.getUserById(uid);
    }
}
