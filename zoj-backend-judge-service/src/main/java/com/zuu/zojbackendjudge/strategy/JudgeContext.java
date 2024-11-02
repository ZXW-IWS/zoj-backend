package com.zuu.zojbackendjudge.strategy;

import com.zuu.domain.po.Question;
import com.zuu.domain.po.QuestionSubmit;
import com.zuu.domain.vo.req.question_submit.JudgeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author zuu
 * @Description 判题信息上下文，用于判题类执行判题代码时传递信息
 * @Date 2024/10/23 17:19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JudgeContext {
    /**
     * 沙箱结果
     */
    private Integer status;
    /**
     * 代码执行输出（沙箱）
     */
    List<String> boxOutput;
    /**
     * 代码执行信息(沙箱)
     */
    JudgeInfo judgeInfo;
    /**
     * 对应的题目
     */
    Question question;
    /**
     * 题目提交信息
     */
    QuestionSubmit questionSubmit;
}
