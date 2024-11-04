package com.zuu.zojbackendquestion.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zuu.domain.dto.QuestionSubmitDto;
import com.zuu.domain.enums.CodeLanguageEnum;
import com.zuu.domain.enums.QuestionSubmitStatusEnum;
import com.zuu.domain.po.Question;
import com.zuu.domain.po.QuestionSubmit;
import com.zuu.domain.vo.req.question_submit.JudgeInfo;
import com.zuu.domain.vo.req.question_submit.QuestionSubmitPageReq;
import com.zuu.domain.vo.req.question_submit.QuestionSubmitReq;
import com.zuu.zojbackendapi.client.JudgeFeignClient;
import com.zuu.zojbackendapi.client.UserFeignClient;
import com.zuu.zojbackendcommon.domain.ErrorEnum;
import com.zuu.zojbackendcommon.exception.BusinessException;
import com.zuu.zojbackendquestion.mapper.QuestionSubmitMapper;
import com.zuu.zojbackendquestion.service.QuestionService;
import com.zuu.zojbackendquestion.service.QuestionSubmitService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author zuu
 * @description 针对表【question_submit】的数据库操作Service实现
 * @createDate 2024-10-19 16:10:34
 */
@Service
@RequiredArgsConstructor
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {
    public final QuestionService questionService;
    public final UserFeignClient userFeignClient;
    public final JudgeFeignClient judgeFeignClient;

    @Override
    public Page<QuestionSubmitDto> getPage(QuestionSubmitPageReq questionSubmitPageReq) {
        String language = questionSubmitPageReq.getLanguage();
        Integer status = questionSubmitPageReq.getStatus();
        Long questionId = questionSubmitPageReq.getQuestionId();
        Long userId = questionSubmitPageReq.getUserId();
        Integer current = questionSubmitPageReq.getCurrent();
        Integer pageSize = questionSubmitPageReq.getPageSize();
        //组装queryWrapper
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Objects.nonNull(questionId) && questionId > 0, "question_id", questionId);
        queryWrapper.eq(Objects.nonNull(userId) && userId > 0, "user_id", userId);
        queryWrapper.eq(Objects.nonNull(status) && status >= 0, "status", status);
        queryWrapper.like(StrUtil.isNotBlank(language), "language", language);
        queryWrapper.orderByDesc("create_time");

        Page<QuestionSubmit> page = this.page(new Page<>(current, pageSize), queryWrapper);
        //Question转DTO
        List<QuestionSubmitDto> questionDtoList = page.getRecords().stream().map(this::toDto).toList();
        Page<QuestionSubmitDto> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        dtoPage.setRecords(questionDtoList);

        return dtoPage;
    }

    /**
     * 提交题目
     *
     * @param token
     * @param questionSubmitReq
     * @return
     */
    @Override
    public Long submitQuestion(String token, QuestionSubmitReq questionSubmitReq) {
        Long uid = userFeignClient.getIdByToken(token);
        String code = questionSubmitReq.getCode();
        String language = questionSubmitReq.getLanguage();
        Long questionId = questionSubmitReq.getQuestionId();
        Question question = questionService.getById(questionId);
        if(Objects.isNull(question)){
            throw new BusinessException(ErrorEnum.NULL_ERROR,"题目" + questionId + "不存在");
        }
        CodeLanguageEnum languageEnum = CodeLanguageEnum.getLanguageEnum(language);
        if(Objects.isNull(languageEnum)){
            throw new BusinessException(ErrorEnum.PARAM_ERROR,"不支持该语言");
        }
        //先保存题目提交信息
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(uid);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(code);
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getStatus());
        questionSubmit.setJudgeInfo("{}");

        this.save(questionSubmit);
        //异步执行判题逻辑
        CompletableFuture.runAsync(() -> {
            judgeFeignClient.doJudge(questionSubmit.getId());
        });
        return questionSubmit.getId();
    }

    private QuestionSubmitDto toDto(QuestionSubmit questionSubmit) {
        QuestionSubmitDto questionSubmitDto = new QuestionSubmitDto();
        BeanUtils.copyProperties(questionSubmit, questionSubmitDto);
        if (StrUtil.isNotBlank(questionSubmit.getJudgeInfo())) {
            questionSubmitDto.setJudgeInfo(JSONUtil.toBean(questionSubmit.getJudgeInfo(), JudgeInfo.class));
        }

        questionSubmitDto.setQuestionInfo(questionService.getQuestionById(questionSubmit.getQuestionId()));
        questionSubmitDto.setUserInfo(userFeignClient.getUserById(questionSubmit.getUserId()));

        return questionSubmitDto;
    }
}




