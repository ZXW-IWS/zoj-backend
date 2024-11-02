package com.zuu.zojbackenduser.controller;
import com.zuu.domain.dto.UserDto;
import com.zuu.domain.vo.req.user.UserLoginReq;
import com.zuu.domain.vo.req.user.UserRegisterReq;
import com.zuu.zojbackendcommon.domain.ApiResult;
import com.zuu.zojbackendcommon.domain.BaseResponse;
import com.zuu.zojbackendcommon.utils.RequestHolder;
import com.zuu.zojbackenduser.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.zuu.zojbackendcommon.constant.UserConstant.TOKEN_HEADER_KEY;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/17 18:36
 */
@RestController
@RequestMapping
@Tag(name = "用户相关接口")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/public/register")
    @Operation(summary = "用户注册接口")
    public BaseResponse<Long> register(@RequestBody UserRegisterReq userRegisterReq) {
        String username = userRegisterReq.getUsername();
        String userPassword = userRegisterReq.getUserPassword();
        String checkPassword = userRegisterReq.getCheckPassword();
        Long uid = userService.userRegister(username, userPassword, checkPassword);
        return ApiResult.success(uid);
    }

    @PostMapping("/public/login")
    @Operation(summary = "用户登录接口")
    public BaseResponse<String> login(@RequestBody UserLoginReq userLoginReq) {
        String username = userLoginReq.getUsername();
        String userPassword = userLoginReq.getUserPassword();
        String token = userService.userLogin(username, userPassword);
        return ApiResult.success(token);
    }

    @GetMapping("/current")
    public BaseResponse<UserDto> getCurrentUser(HttpServletRequest request){
        String token = request.getHeader(TOKEN_HEADER_KEY);
        Long uid = userService.getIdByToken(token);
        return ApiResult.success(userService.getUserById(uid));
    }
}
