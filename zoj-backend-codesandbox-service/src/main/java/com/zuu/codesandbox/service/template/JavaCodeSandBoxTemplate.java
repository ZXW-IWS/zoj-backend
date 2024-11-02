package com.zuu.codesandbox.service.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.zuu.codesandbox.domain.ExecuteResp;
import com.zuu.codesandbox.service.CodeSandBox;
import com.zuu.codesandbox.utils.ProcessUtils;
import com.zuu.domain.enums.SandBoxExecuteStatusEnum;
import com.zuu.domain.vo.req.question_submit.JudgeInfo;
import com.zuu.domain.vo.req.sandbox.ExecuteCodeReq;
import com.zuu.domain.vo.resp.sandbox.ExecuteCodeResp;


import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/27 14:16
 */
public abstract class JavaCodeSandBoxTemplate implements CodeSandBox {
    public static final String CODE_DIR_PATH = "code";
    public static final String JAVA_FILE_NAME = "Main";
    public static final String JAVA_FILE_SUFFIX = ".java";
    public static final String JAVA_CLASS_SUFFIX = ".class";

    @Override
    public ExecuteCodeResp executeCode(ExecuteCodeReq executeCodeReq) {
        String code = executeCodeReq.getCode();
        String language = executeCodeReq.getLanguage();
        List<String> input = executeCodeReq.getInput();
        //1. 保存代码到Main.java文件
        File codeFile = savaFile(code);
        //2. 编译代码文件
        ExecuteResp compileExecuteResp = compileFile(codeFile);
        //编译失败返回
        if (compileExecuteResp.getExecCode() != 0){
            ExecuteCodeResp executeCodeResp = new ExecuteCodeResp();
            executeCodeResp.setStatus(SandBoxExecuteStatusEnum.COMPILE_ERROR.getStatus());
            executeCodeResp.setMessage(compileExecuteResp.getErrorMessage());
            return executeCodeResp;
        }
        //3. 运行代码，得到运行结果
        List<ExecuteResp> runExecuteResp = runFile(codeFile.getParent(), input);
        System.out.println("list resp:" + runExecuteResp);
        //4.处理结果为结果返回的格式
        ExecuteCodeResp executeCodeResp = getFinalResp(runExecuteResp);
        //5.删除文件
        if (codeFile.getParentFile() != null) {
            FileUtil.del(codeFile.getParent());
        }
        return executeCodeResp;
    }


    /**
     * 1. 保存代码文件
     *
     * @param code
     * @return
     */
    public File savaFile(String code) {
        String rootPath = System.getProperty("user.dir") + File.separator + CODE_DIR_PATH;
        String userPath = rootPath + File.separator + UUID.randomUUID();
        String filePath = userPath + File.separator + JAVA_FILE_NAME + JAVA_FILE_SUFFIX;

        return FileUtil.writeString(code, filePath, StandardCharsets.UTF_8);
    }

    /**
     * 2.编译代码
     *
     * @param codefile
     * @return
     */
    public ExecuteResp compileFile(File codefile) {
        String compileCommand = String.format("javac -encoding utf-8 %s", codefile.getAbsolutePath());
        return ProcessUtils.runCommand(compileCommand, "编译", null);
    }

    /**
     * 3. 运行代码并获取每个输入的结果；
     * 该方法由不同的子类进行实现
     *
     * @param dirPath class文件所在目录的绝对路径
     * @param input   输入列表
     * @return
     */
    public abstract List<ExecuteResp> runFile(String dirPath, List<String> input);

    /**
     * 4. 根据运行的结果得到最终的api返回
     *
     * @param runExecuteResp
     * @return
     */
    public ExecuteCodeResp getFinalResp(List<ExecuteResp> runExecuteResp) {
        List<String> outputList = new ArrayList<>();
        long maxTime = 0;
        int maxMemory = 0;
        ExecuteCodeResp executeCodeResp = new ExecuteCodeResp();
        executeCodeResp.setStatus(SandBoxExecuteStatusEnum.SUCCESS.getStatus());
        for (ExecuteResp executeResp : runExecuteResp) {
            Integer execCode = executeResp.getExecCode();
            String message = executeResp.getErrorMessage();
            String output = executeResp.getOutput();
            Long time = executeResp.getTime();
            int memory = executeResp.getMemory();
            if (execCode != 0) {
                //执行过程中出现错误，异常返回
                executeCodeResp.setMessage(message);
                executeCodeResp.setStatus(SandBoxExecuteStatusEnum.RUN_ERROR.getStatus());
                break;
            }
            outputList.add(output);
            if (Objects.nonNull(time))
                maxTime = Math.max(maxTime, time);
            maxMemory = Math.max(maxMemory, memory);
        }
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMemory(maxMemory);
        judgeInfo.setTime((int) maxTime);

        executeCodeResp.setOutput(outputList);
        executeCodeResp.setJudgeInfo(judgeInfo);
        return executeCodeResp;
    }
}
