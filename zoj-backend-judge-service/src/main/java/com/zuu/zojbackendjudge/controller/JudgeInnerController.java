package com.zuu.zojbackendjudge.controller;

import com.zuu.zojbackendjudge.service.JudgeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/29 20:41
 */
@RestController
@RequestMapping("/inner")
public class JudgeInnerController {
    @Resource
    JudgeService judgeService;
    @PostMapping("/do")
    Void doJudge(@RequestParam("questionSubmitId")Long questionSubmitId){
        judgeService.doJudge(questionSubmitId);
        return null;
    }
}
