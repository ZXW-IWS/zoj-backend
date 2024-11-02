package com.zuu.zojbackendjudge.codesandbox.strategy;

import com.zuu.domain.enums.CodeSandBoxEnum;
import com.zuu.domain.vo.req.question_submit.JudgeInfo;
import com.zuu.domain.vo.req.sandbox.ExecuteCodeReq;
import com.zuu.domain.vo.resp.sandbox.ExecuteCodeResp;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/23 14:52
 */
@Component
public class ExampleCodeSandBox implements CodeSandBox{
    /**
     * 获取代码沙箱类型
     *
     * @return
     */
    @Override
    public CodeSandBoxEnum getCodeSandBoxEnum() {
        return CodeSandBoxEnum.EXAMPLE;
    }

    /**
     * 执行代码的逻辑
     *
     * @param executeCodeReq
     * @return
     */
    @Override
    public ExecuteCodeResp executeCode(ExecuteCodeReq executeCodeReq) {
        System.out.println("示例代码沙箱");
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(1);
        judgeInfo.setMemory(1);
        return ExecuteCodeResp.builder()
                .output(List.of(""))
                .judgeInfo(judgeInfo)
                .build();
    }
}
