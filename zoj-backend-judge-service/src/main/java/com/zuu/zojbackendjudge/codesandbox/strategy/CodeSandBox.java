package com.zuu.zojbackendjudge.codesandbox.strategy;

import com.zuu.domain.enums.CodeSandBoxEnum;
import com.zuu.domain.vo.req.sandbox.ExecuteCodeReq;
import com.zuu.domain.vo.resp.sandbox.ExecuteCodeResp;
import com.zuu.zojbackendjudge.codesandbox.factory.CodeSandBoxFactory;
import jakarta.annotation.PostConstruct;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/23 14:30
 */
public interface CodeSandBox {
    /**
     * 初始化时将代码沙箱加入到工厂中
     */
    @PostConstruct
    public default void init(){
        CodeSandBoxFactory.register(this.getCodeSandBoxEnum(),this);
    }

    /**
     * 获取代码沙箱类型
     * @return
     */
    public CodeSandBoxEnum getCodeSandBoxEnum();

    /**
     * 执行代码的逻辑
     * @param executeCodeReq
     * @return
     */
    public ExecuteCodeResp executeCode(ExecuteCodeReq executeCodeReq);
}
