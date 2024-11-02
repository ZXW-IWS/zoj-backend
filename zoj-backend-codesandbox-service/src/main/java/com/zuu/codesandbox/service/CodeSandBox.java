package com.zuu.codesandbox.service;


import com.zuu.domain.vo.req.sandbox.ExecuteCodeReq;
import com.zuu.domain.vo.resp.sandbox.ExecuteCodeResp;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/23 14:30
 */
public interface CodeSandBox {

    /**
     * 执行代码的逻辑
     * @param executeCodeReq
     * @return
     */
    public ExecuteCodeResp executeCode(ExecuteCodeReq executeCodeReq);
}
