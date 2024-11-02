package com.zuu.zojbackendjudge.strategy;
import com.zuu.domain.enums.CodeLanguageEnum;
import com.zuu.domain.vo.req.question_submit.JudgeInfo;
import com.zuu.zojbackendjudge.factory.JudgeHandleFactory;
import jakarta.annotation.PostConstruct;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/23 17:17
 */
public interface JudgeHandleStrategy {
    @PostConstruct
    default void init(){
        JudgeHandleFactory.register(this.getCodeLanguageEnum(),this);
    }
    CodeLanguageEnum getCodeLanguageEnum();
    JudgeInfo doJudge(JudgeContext judgeContext);
}
