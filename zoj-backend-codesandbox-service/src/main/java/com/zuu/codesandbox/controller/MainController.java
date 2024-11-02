package com.zuu.codesandbox.controller;

import com.zuu.codesandbox.service.template.JavaDockerCodeSandBox;
import com.zuu.domain.vo.req.sandbox.ExecuteCodeReq;
import com.zuu.domain.vo.resp.sandbox.ExecuteCodeResp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/24 11:24
 */
@RestController
public class MainController {

    @Resource
    JavaDockerCodeSandBox javaDockerCodeSandBox;
    @GetMapping("/health")
    public String healthCheck(){
        return "ok";
    }

    @PostMapping("/exec_code")
    public ExecuteCodeResp execCode(@RequestBody ExecuteCodeReq executeCodeReq){
        return javaDockerCodeSandBox.executeCode(executeCodeReq);
    }
}
