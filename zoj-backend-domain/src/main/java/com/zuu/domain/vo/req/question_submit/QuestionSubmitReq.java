package com.zuu.domain.vo.req.question_submit;

import lombok.Data;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/23 16:17
 */
@Data
public class QuestionSubmitReq {
    /**
     * 代码
     */
    private String code;
    /**
     * 语言
     */
    private String language;
    /**
     * 题目id
     */
    private Long questionId;
}
