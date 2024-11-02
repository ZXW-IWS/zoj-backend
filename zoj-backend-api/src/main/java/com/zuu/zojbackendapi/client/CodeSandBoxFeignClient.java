package com.zuu.zojbackendapi.client;

import com.zuu.domain.vo.req.sandbox.ExecuteCodeReq;
import com.zuu.domain.vo.resp.sandbox.ExecuteCodeResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author zuu
 * @Description
 * @Date 2024/11/2 12:40
 */
@FeignClient(name = "codesandbox-service",path = "/api/codesandbox")
public interface CodeSandBoxFeignClient {
    @PostMapping("/exec_code")
    ExecuteCodeResp execCode(@RequestBody ExecuteCodeReq executeCodeReq);
}
