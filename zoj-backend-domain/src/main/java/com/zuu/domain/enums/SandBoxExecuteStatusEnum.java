package com.zuu.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/27 14:56
 */
@AllArgsConstructor
@Getter
public enum SandBoxExecuteStatusEnum {
    SUCCESS(0,"代码成功执行"),
    COMPILE_ERROR(1,"编译错误"),
    RUN_ERROR(2,"运行错误"),
    ;
    private final Integer status;
    private final String desc;
}
