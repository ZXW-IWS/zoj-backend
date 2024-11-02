package com.zuu.zojbackendjudge.codesandbox.proxy;

import com.zuu.domain.enums.CodeSandBoxEnum;
import com.zuu.domain.vo.req.sandbox.ExecuteCodeReq;
import com.zuu.domain.vo.resp.sandbox.ExecuteCodeResp;
import com.zuu.zojbackendjudge.codesandbox.strategy.CodeSandBox;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/23 15:10
 */
@Slf4j
public class CodeSandBoxProxy implements CodeSandBox {
    private final CodeSandBox codeSandBox;
    public CodeSandBoxProxy(CodeSandBox codeSandBox){
        this.codeSandBox = codeSandBox;
    }
    /**
     * 获取代码沙箱类型
     *
     * @return
     */
    @Override
    public CodeSandBoxEnum getCodeSandBoxEnum() {
        return codeSandBox.getCodeSandBoxEnum();
    }

    /**
     * 执行代码的逻辑
     *
     * @param executeCodeReq
     * @return
     */
    @Override
    public ExecuteCodeResp executeCode(ExecuteCodeReq executeCodeReq) {
        log.info("输入代码:{}",executeCodeReq.getCode());
        ExecuteCodeResp executeCodeResp = codeSandBox.executeCode(executeCodeReq);
        log.info("输出结果:{}",executeCodeResp.toString());
        return executeCodeResp;
    }
}
