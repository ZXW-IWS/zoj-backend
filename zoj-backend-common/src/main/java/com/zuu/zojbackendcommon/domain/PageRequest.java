package com.zuu.zojbackendcommon.domain;

import lombok.Data;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/20 10:49
 */
@Data
public class PageRequest {
    /**
     * 当前页码
     */
    private Integer current;
    /**
     * 页面大小
     */
    private Integer pageSize;
}
