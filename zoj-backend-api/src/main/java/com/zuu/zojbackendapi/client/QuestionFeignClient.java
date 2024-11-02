package com.zuu.zojbackendapi.client;

import com.zuu.domain.po.Question;
import com.zuu.domain.po.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/29 20:06
 */
@FeignClient(name = "question-service",path = "/api/question/inner")
public interface QuestionFeignClient {
    @GetMapping("/submit/id")
    QuestionSubmit getSubmitById(@RequestParam("questionSubmitId") Long questionSubmitId);
    @PutMapping("/submit/update")
    Boolean updateSubmitById(@RequestBody QuestionSubmit questionSubmit);
    @GetMapping("/id")
    Question getById(@RequestParam("questionId") Long questionId);
    @PostMapping("/plus/submit")
    Void submitCountPlus(@RequestParam("questionId") Long questionId);
    @PostMapping("/plus/accept")
    Void newAccept(@RequestParam("questionId") Long questionId);
}
