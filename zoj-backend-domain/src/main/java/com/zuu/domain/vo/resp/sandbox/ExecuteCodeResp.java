package com.zuu.domain.vo.resp.sandbox;

import com.zuu.domain.vo.req.question_submit.JudgeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/23 14:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecuteCodeResp {
    /**
     * 代码输出
     */
    private List<String> output;
    /**
     * 执行信息
     */
    private JudgeInfo judgeInfo;
    /**
     * 接口信息
     */
    private String message;
    /**
     * 执行状态
     */
    private Integer status;
}
