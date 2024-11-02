package com.zuu.zojbackendquestion.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zuu.domain.dto.QuestionSubmitDto;
import com.zuu.domain.vo.req.question_submit.QuestionSubmitPageReq;
import com.zuu.domain.vo.req.question_submit.QuestionSubmitReq;
import com.zuu.zojbackendcommon.domain.ApiResult;
import com.zuu.zojbackendcommon.domain.BaseResponse;
import com.zuu.zojbackendcommon.utils.RequestHolder;
import com.zuu.zojbackendquestion.service.QuestionSubmitService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.zuu.zojbackendcommon.constant.UserConstant.TOKEN_HEADER_KEY;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/20 12:16
 */
@RestController
@RequestMapping("/submit")
@Tag(name = "题目提交相关接口")
public class QuestionSubmitController {
    @Resource
    private QuestionSubmitService questionSubmitService;

    /**
     * 分页获取题目提交信息
     * @return
     */
    @PostMapping("/public/submit/list")
    public BaseResponse<Page<QuestionSubmitDto>> getPage(@RequestBody QuestionSubmitPageReq questionSubmitPageReq){
        return ApiResult.success(questionSubmitService.getPage(questionSubmitPageReq));
    }

    @PostMapping()
    public BaseResponse<Long> submitQuestion(@RequestBody QuestionSubmitReq questionSubmitReq, HttpServletRequest request){
        String token = request.getHeader(TOKEN_HEADER_KEY);
        return ApiResult.success(questionSubmitService.submitQuestion(token,questionSubmitReq));
    }
}
