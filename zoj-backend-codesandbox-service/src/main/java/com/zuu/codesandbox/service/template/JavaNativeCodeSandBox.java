package com.zuu.codesandbox.service.template;

import com.zuu.codesandbox.domain.ExecuteResp;
import com.zuu.codesandbox.utils.ProcessUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/27 15:34
 */
@Component
public class JavaNativeCodeSandBox extends JavaCodeSandBoxTemplate{
    /**
     * 3. 运行代码并获取每个输入的结果；
     * 该方法由不同的子类进行实现
     *
     * @param dirPath class文件所在目录的绝对路径
     * @param input   输入列表
     * @return
     */
    @Override
    public List<ExecuteResp> runFile(String dirPath, List<String> input) {
        String runCommand = String.format("java -Xmx256m -D'file.encoding=UTF-8' %s %s",dirPath,JAVA_FILE_NAME);
        List<ExecuteResp> executeRespList = new ArrayList<>();
        for (String inputCase : input) {
            ExecuteResp executeResp = ProcessUtils.runCommandWithTimeLimit(runCommand, "运行", inputCase,2000);
            executeRespList.add(executeResp);
        }

        return executeRespList;
    }
}
