package com.zuu.zojbackendjudge.strategy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.zuu.domain.enums.CodeLanguageEnum;
import com.zuu.domain.enums.JudgeInfoMsgEnum;
import com.zuu.domain.enums.SandBoxExecuteStatusEnum;
import com.zuu.domain.po.Question;
import com.zuu.domain.po.QuestionSubmit;
import com.zuu.domain.vo.req.question.JudgeCase;
import com.zuu.domain.vo.req.question.JudgeConfig;
import com.zuu.domain.vo.req.question_submit.JudgeInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author zuu
 * @Description 执行具体判题逻辑的类
 * @Date 2024/10/23 17:45
 */
@Component
public class JavaJudgeHandle implements JudgeHandleStrategy {
    @Override
    public CodeLanguageEnum getCodeLanguageEnum() {
        return CodeLanguageEnum.JAVA;
    }

    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        System.out.println("java判题");
        List<String> boxOutput = judgeContext.getBoxOutput();
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Question question = judgeContext.getQuestion();
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        Integer status = judgeContext.getStatus();

        JudgeInfo judgeInfoResp = new JudgeInfo();

        //校验代码沙箱编译运行是否正确
        if(Objects.nonNull(status) && !SandBoxExecuteStatusEnum.SUCCESS.getStatus().equals(status)){
            if(SandBoxExecuteStatusEnum.RUN_ERROR.getStatus().equals(status)){
                judgeInfoResp.setMessage(JudgeInfoMsgEnum.RUN_ERROR.getJudgeMessage());
            }else{
                judgeInfoResp.setMessage(JudgeInfoMsgEnum.COMPILE_ERROR.getJudgeMessage());
            }
            return judgeInfoResp;
        }

        //1. 校验沙箱输出结果与预期输出是否一致
        List<JudgeCase> judgeCase = JSONUtil.toList(question.getJudgeCase(), JudgeCase.class);
        List<String> exceptedOutput = judgeCase.stream().map(JudgeCase::getOutput).toList();
        //若大小不同，则结果一定错误
        if(CollectionUtil.size(boxOutput) != CollectionUtil.size(exceptedOutput)){
            judgeInfoResp.setMessage(JudgeInfoMsgEnum.WRONG_ANSWER.getJudgeMessage());
            return judgeInfoResp;
        }
        for (int i = 0;i < exceptedOutput.size();i++) {
            if(!exceptedOutput.get(i).equals(boxOutput.get(i))){
                judgeInfoResp.setMessage(JudgeInfoMsgEnum.WRONG_ANSWER.getJudgeMessage());
                return judgeInfoResp;
            }
        }
        //2. 校验沙箱输出的判题信息中的时间空间是否满足条件
        if(Objects.nonNull(judgeInfo)) {
            JudgeConfig judgeConfig = JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class);
            Integer runMemory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0);
            Integer runTime = Optional.ofNullable(judgeInfo.getTime()).orElse(0);
            judgeInfoResp.setMemory(runMemory);
            judgeInfoResp.setTime(runTime);
            if (runMemory > judgeConfig.getMemoryLimit()) {
                judgeInfoResp.setMessage(JudgeInfoMsgEnum.MEMORY_LIMIT_EXCEEDED.getJudgeMessage());
                return judgeInfoResp;
            }
            if (runTime > judgeConfig.getTimeLimit()) {
                judgeInfoResp.setMessage(JudgeInfoMsgEnum.TIME_LIMIT_EXCEEDED.getJudgeMessage());
                return judgeInfoResp;
            }
        }


        //3.校验成功
        judgeInfoResp.setMessage(JudgeInfoMsgEnum.SUCCESS.getJudgeMessage());
        return judgeInfoResp;
    }
}
