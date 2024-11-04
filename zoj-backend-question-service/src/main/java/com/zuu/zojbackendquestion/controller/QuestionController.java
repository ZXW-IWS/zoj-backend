package com.zuu.zojbackendquestion.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zuu.domain.dto.QuestionAdminDto;
import com.zuu.domain.dto.QuestionDto;
import com.zuu.domain.vo.req.question.QuestionAddReq;
import com.zuu.domain.vo.req.question.QuestionDeleteReq;
import com.zuu.domain.vo.req.question.QuestionPageReq;
import com.zuu.domain.vo.req.question.QuestionUpdateReq;
import com.zuu.zojbackendcommon.constant.UserConstant;
import com.zuu.zojbackendcommon.domain.ApiResult;
import com.zuu.zojbackendcommon.domain.BaseResponse;
import com.zuu.zojbackendcommon.utils.RequestHolder;
import com.zuu.zojbackendquestion.service.QuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import static com.zuu.zojbackendcommon.constant.UserConstant.TOKEN_HEADER_KEY;


/**
 * @Author zuu
 * @Description
 * @Date 2024/10/19 16:55
 */
@RestController
@RequestMapping
@Tag(name = "题目相关接口")
public class QuestionController {
    @Resource
    public QuestionService questionService;
    // region 增删改查

    /**
     * 创建
     *
     * @param questionAddReq
     * @return
     */
    @PutMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddReq questionAddReq,HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER_KEY);
        Long questionId = questionService.addQuestion(token,questionAddReq);

        return ApiResult.success(questionId);
    }

    /**
     * 删除
     * @param questionDeleteReq
     * @return
     */
    @DeleteMapping("/delete")
    public BaseResponse<Void> deleteQuestion(@RequestBody QuestionDeleteReq questionDeleteReq, HttpServletRequest request){
        String token = request.getHeader(TOKEN_HEADER_KEY);
        questionService.deleteQuestion(token,questionDeleteReq);

        return ApiResult.success();
    }

    /**
     * 更新
     * @param questionUpdateReq
     * @return
     */
    @PutMapping("/update")
    public BaseResponse<Void> updateQuestion(@RequestBody QuestionUpdateReq questionUpdateReq){
        questionService.updateQuestion(questionUpdateReq);

        return ApiResult.success();
    }
    /**
     * 根据id获取题目(仅管理员更新时获取)
     * @param questionId
     * @return
     */
    @GetMapping("admin/get/{questionId}")
    public BaseResponse<QuestionAdminDto> adminGetById(@PathVariable("questionId") Long questionId){
        return ApiResult.success(questionService.adminGet(questionId));
    }

    /**
     * 根据id获取题目
     * @param questionId
     * @return
     */
    @GetMapping("/get/{questionId}")
    public BaseResponse<QuestionDto> getById(@PathVariable("questionId") Long questionId){
        return ApiResult.success(questionService.getQuestionById(questionId));
    }

    /**
     * 根据条件分页获取题目
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionDto>> getByPage(@RequestBody QuestionPageReq questionPageReq){
        return ApiResult.success(questionService.getByPage(questionPageReq));
    }
    // endregion
}
