package com.zuu.zojbackendjudge.codesandbox.strategy;

import com.zuu.domain.enums.CodeSandBoxEnum;
import com.zuu.domain.vo.req.sandbox.ExecuteCodeReq;
import com.zuu.domain.vo.resp.sandbox.ExecuteCodeResp;
import org.springframework.stereotype.Component;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/23 14:53
 */
@Component
public class ThirdCodeSandBox implements CodeSandBox{
    /**
     * 获取代码沙箱类型
     *
     * @return
     */
    @Override
    public CodeSandBoxEnum getCodeSandBoxEnum() {
        return CodeSandBoxEnum.THIRD;
    }

    /**
     * 执行代码的逻辑
     *
     * @param executeCodeReq
     * @return
     */
    @Override
    public ExecuteCodeResp executeCode(ExecuteCodeReq executeCodeReq) {
        System.out.println("第三方代码沙箱");
        return new ExecuteCodeResp();
    }
}
