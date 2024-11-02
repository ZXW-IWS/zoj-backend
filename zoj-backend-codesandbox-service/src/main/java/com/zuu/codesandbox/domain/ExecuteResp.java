package com.zuu.codesandbox.domain;

import lombok.Data;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/24 17:13
 */
@Data
public class ExecuteResp {
    /**
     * 命令执行结果
     */
    private Integer execCode;
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 成功信息（即输出）
     */
    private String output;
    private Long time;
    private int memory;
}
