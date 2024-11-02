package com.zuu.zojbackendquestion.controller.inner;

import com.zuu.domain.po.Question;
import com.zuu.domain.po.QuestionSubmit;
import com.zuu.zojbackendquestion.service.QuestionService;
import com.zuu.zojbackendquestion.service.QuestionSubmitService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/29 20:26
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController {
    @Resource
    QuestionService questionService;
    @Resource
    QuestionSubmitService questionSubmitService;
    @GetMapping("/submit/id")
    QuestionSubmit getSubmitById(@RequestParam("questionSubmitId")Long questionSubmitId){
        return questionSubmitService.getById(questionSubmitId);
    }
    @PutMapping("/submit/update")
    Boolean updateSubmitById(@RequestBody QuestionSubmit questionSubmit){
        return questionSubmitService.updateById(questionSubmit);
    }
    @GetMapping("/id")
    Question getById(@RequestParam("questionId")Long questionId){
        return questionService.getById(questionId);
    }
    @PostMapping("/plus/submit")
    Void submitCountPlus(@RequestParam("questionId")Long questionId){
        questionService.submitCountPlus(questionId);
        return null;
    }
    @PostMapping("/plus/accept")
    Void newAccept(@RequestParam("questionId")Long questionId){
        questionService.newAccept(questionId);
        return null;
    }
}
