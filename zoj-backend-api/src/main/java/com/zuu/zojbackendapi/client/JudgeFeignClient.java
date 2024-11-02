package com.zuu.zojbackendapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/29 20:32
 */
@FeignClient(name = "judge-service",path = "/api/judge/inner")
public interface JudgeFeignClient {
    @PostMapping("/do")
    Void doJudge(@RequestParam("questionSubmitId") Long questionSubmitId);
}
