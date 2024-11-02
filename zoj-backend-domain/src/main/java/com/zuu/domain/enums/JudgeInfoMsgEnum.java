package com.zuu.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/20 12:50
 */
@Getter
@AllArgsConstructor
public enum JudgeInfoMsgEnum {
    SUCCESS("Success","运行成功"),
    WRONG_ANSWER("Wrong Answer", "答案错误"),
    COMPILE_ERROR("Compile Error","编译错误"),
    RUN_ERROR("Run Error","运行错误"),
    MEMORY_LIMIT_EXCEEDED("Memory Limit Exceeded", "内存溢出"),
    TIME_LIMIT_EXCEEDED("Time Limit Exceeded", "超时"),
    ;
    private final String judgeMessage;
    private final String desc;
}
