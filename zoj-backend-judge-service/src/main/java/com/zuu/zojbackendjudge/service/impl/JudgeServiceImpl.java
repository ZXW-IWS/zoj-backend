package com.zuu.zojbackendjudge.service.impl;

import cn.hutool.json.JSONUtil;
import com.zuu.domain.enums.JudgeInfoMsgEnum;
import com.zuu.domain.enums.QuestionSubmitStatusEnum;
import com.zuu.domain.po.Question;
import com.zuu.domain.po.QuestionSubmit;
import com.zuu.domain.vo.req.question.JudgeCase;
import com.zuu.domain.vo.req.question_submit.JudgeInfo;
import com.zuu.domain.vo.req.sandbox.ExecuteCodeReq;
import com.zuu.domain.vo.resp.sandbox.ExecuteCodeResp;
import com.zuu.zojbackendapi.client.QuestionFeignClient;
import com.zuu.zojbackendcommon.domain.ErrorEnum;
import com.zuu.zojbackendcommon.exeption.BusinessException;
import com.zuu.zojbackendjudge.codesandbox.factory.CodeSandBoxFactory;
import com.zuu.zojbackendjudge.codesandbox.proxy.CodeSandBoxProxy;
import com.zuu.zojbackendjudge.codesandbox.strategy.CodeSandBox;
import com.zuu.zojbackendjudge.factory.JudgeHandleFactory;
import com.zuu.zojbackendjudge.service.JudgeService;
import com.zuu.zojbackendjudge.strategy.JudgeContext;
import com.zuu.zojbackendjudge.strategy.JudgeHandleStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/23 16:40
 */
@Service
@RequiredArgsConstructor
public class JudgeServiceImpl implements JudgeService {
    private final QuestionFeignClient questionFeignClient;

    @Value(value = "${code-sand-box.type}")
    public String type;
    /**
     * 执行判题
     *
     * @param questionSubmitId
     */
    @Override
    public void doJudge(Long questionSubmitId) {
        QuestionSubmit questionSubmit = questionFeignClient.getSubmitById(questionSubmitId);
        if(Objects.isNull(questionSubmit)){
            throw new BusinessException(ErrorEnum.NULL_ERROR,"题目提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        Integer status = questionSubmit.getStatus();
        Question question = questionFeignClient.getById(questionId);
        if(Objects.isNull(question)){
            throw new BusinessException(ErrorEnum.NULL_ERROR,"题目不存在");
        }
        //1.若题目不是待判题状态，就不必要重复判题了
        if(!QuestionSubmitStatusEnum.WAITING.getStatus().equals(status)){
            return;
        }
        //修改题目状态，防止重复判题
        questionSubmit.setStatus(QuestionSubmitStatusEnum.RUNNING.getStatus());
        questionFeignClient.updateSubmitById(questionSubmit);
        //题目提交数自增
        questionFeignClient.submitCountPlus(question.getId());
        //2.调用代码沙箱执行代码
        List<JudgeCase> judgeCase = JSONUtil.toList(question.getJudgeCase(), JudgeCase.class);
        List<String> inputList = judgeCase.stream().map(JudgeCase::getInput).toList();
        ExecuteCodeReq executeCodeReq = ExecuteCodeReq.builder()
                .code(code)
                .language(language)
                .input(inputList)
                .build();
        CodeSandBox codeSandBox = CodeSandBoxFactory.getIfAbsent(type);
        CodeSandBoxProxy codeSandBoxProxy = new CodeSandBoxProxy(codeSandBox);
        ExecuteCodeResp executeCodeResp = codeSandBoxProxy.executeCode(executeCodeReq);
        //3.进行判题
        JudgeHandleStrategy judgeHandle = JudgeHandleFactory.getIfAbsent(language);
        JudgeContext judgeContext = JudgeContext.builder()
                .status(executeCodeResp.getStatus())
                .judgeInfo(executeCodeResp.getJudgeInfo())
                .boxOutput(executeCodeResp.getOutput())
                .question(question)
                .questionSubmit(questionSubmit)
                .build();

        JudgeInfo judgeInfo = judgeHandle.doJudge(judgeContext);
        //更新判题结果
        questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        if(JudgeInfoMsgEnum.SUCCESS.getJudgeMessage().equals(judgeInfo.getMessage())){
            questionSubmit.setStatus(QuestionSubmitStatusEnum.SUCCESS.getStatus());
            questionFeignClient.newAccept(question.getId());
        }else{
            questionSubmit.setStatus(QuestionSubmitStatusEnum.FALSE.getStatus());
        }
        questionFeignClient.updateSubmitById(questionSubmit);
    }
}
