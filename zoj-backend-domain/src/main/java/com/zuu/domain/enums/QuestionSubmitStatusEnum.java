package com.zuu.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/23 16:36
 */
@AllArgsConstructor
@Getter
public enum QuestionSubmitStatusEnum {
    WAITING(0,"待判题"),
    RUNNING(1,"判题中"),
    SUCCESS(2,"判题成功"),
    FALSE(3,"判题失败"),
    ;
    private final Integer status;
    private final String desc;
}
