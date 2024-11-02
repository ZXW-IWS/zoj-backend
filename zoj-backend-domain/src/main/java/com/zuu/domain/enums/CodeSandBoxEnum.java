package com.zuu.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/23 14:32
 */
@Getter
@AllArgsConstructor
public enum CodeSandBoxEnum {
    EXAMPLE("example", "执行示例代码的沙箱"),
    REMOTE("remote","远程代码沙箱"),
    THIRD("third","第三方代码沙箱")
    ;
    /**
     * 代码沙箱类型
     */
    private final String type;
    /**
     * 详细描述
     */
    private final String desc;
}
