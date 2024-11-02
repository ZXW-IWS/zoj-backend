package com.zuu.zojbackendjudge.codesandbox.strategy;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.zuu.domain.enums.CodeSandBoxEnum;
import com.zuu.domain.vo.req.sandbox.ExecuteCodeReq;
import com.zuu.domain.vo.resp.sandbox.ExecuteCodeResp;
import com.zuu.zojbackendapi.client.CodeSandBoxFeignClient;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/23 14:53
 */
@Component
public class RemoteCodeSandBox implements CodeSandBox{
    @Value("${code-sand-box.remote-url}")
    String remoteUrl;
    @Resource
    CodeSandBoxFeignClient codeSandBoxFeignClient;
    /**
     * 获取代码沙箱类型
     *
     * @return
     */
    @Override
    public CodeSandBoxEnum getCodeSandBoxEnum() {
        return CodeSandBoxEnum.REMOTE;
    }

    /**
     * 执行代码的逻辑
     *
     * @param executeCodeReq
     * @return
     */
    @Override
    public ExecuteCodeResp executeCode(ExecuteCodeReq executeCodeReq) {
        System.out.println("远程代码沙箱调用");
        //String body = JSONUtil.toJsonStr(executeCodeReq);
        //String execCodeRespJson = HttpUtil.post(remoteUrl, body);
        //return JSONUtil.toBean(execCodeRespJson, ExecuteCodeResp.class);
        return codeSandBoxFeignClient.execCode(executeCodeReq);
    }
}
