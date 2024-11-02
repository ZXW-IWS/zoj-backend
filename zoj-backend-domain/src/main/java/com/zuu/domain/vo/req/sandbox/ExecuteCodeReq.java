package com.zuu.domain.vo.req.sandbox;

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
public class ExecuteCodeReq {
    /**
     * 代码
     */
    private String code;
    /**
     * 编程语言
     */
    private String language;
    /**
     * 输入用例
     */
    private List<String> input;
}
