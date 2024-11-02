package com.zuu.codesandbox.utils;

import cn.hutool.core.util.StrUtil;
import com.zuu.codesandbox.domain.ExecuteResp;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/24 17:05
 */
public class ProcessUtils {

    public static ExecuteResp runCommand(String command, String optName, String arg) {
        return runCommandWithTimeLimit(command, optName, arg, -1L);
    }

    public static ExecuteResp runCommandWithTimeLimit(String command, String optName, String arg, long maxTime) {
        ExecuteResp executeResp = new ExecuteResp();
        String line;
        try {
            Process process = Runtime.getRuntime().exec(command);
            Thread deamonThread = new Thread(() -> {
                try {
                    Thread.sleep(maxTime);
                    System.out.println("超时了呦");
                    if (process.isAlive())
                        process.destroy();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            deamonThread.setDaemon(true);
            if (maxTime > 0) deamonThread.start();
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            PrintWriter printWriter = new PrintWriter(process.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = process.errorReader();
            //程序输入
            if (StrUtil.isNotBlank(arg)) {
                String[] split = arg.split(" ");
                String join = StrUtil.join("\n", split) + "\n";
                printWriter.print(join);
                printWriter.flush();
            }
            //执行命令，获取成功/错误信息
            int execCode = process.waitFor();
            StringBuilder message = new StringBuilder();
            if (execCode == 0) {
                System.out.println(optName + "成功");
                while ((line = bufferedReader.readLine()) != null) {
                    message.append(line).append("\n");
                }
            } else {
                System.out.println(optName + "失败,错误码:" + execCode);
                while ((line = errorReader.readLine()) != null) {
                    message.append(line).append("\n");
                }
            }
            stopWatch.stop();
            System.out.println(message);
            executeResp.setExecCode(execCode);
            executeResp.setErrorMessage(message.toString());
            executeResp.setOutput(message.toString());
            executeResp.setTime(stopWatch.getTotalTimeMillis());

            //关闭，防止卡死
            printWriter.close();
            bufferedReader.close();
            errorReader.close();
            return executeResp;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
