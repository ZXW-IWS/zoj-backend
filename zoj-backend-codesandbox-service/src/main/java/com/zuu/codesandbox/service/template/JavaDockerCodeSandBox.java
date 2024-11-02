package com.zuu.codesandbox.service.template;

import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.ExecStartCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.*;
import com.zuu.codesandbox.domain.ExecuteResp;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/27 15:38
 */
@Component
public class JavaDockerCodeSandBox extends JavaCodeSandBoxTemplate {
    @Value("${docker.java-ghost}")
    String ghost;
    @Resource
    DockerClient dockerClient;

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
        List<ExecuteResp> executeRespList = new ArrayList<>();
        String localFilePath = dirPath + File.separator + JAVA_FILE_NAME + JAVA_CLASS_SUFFIX;
        String runCommand = String.format("java -Xmx256m -D'file.encoding=UTF-8' %s %s", "/app", JAVA_FILE_NAME);
        HostConfig hostConfig = HostConfig.newHostConfig();
        Bind bind = new Bind(localFilePath, new Volume("/app"));
        hostConfig.setBinds(bind);
        //创建容器
        CreateContainerResponse response = dockerClient
                .createContainerCmd(ghost)
                .withMemory(1024 * 1024 * 100L) //内存限制(KB)
                .withNetworkDisabled(true) //禁止使用网络
                .withStopTimeout(30) //设置停止超时时间为30秒
                .withCmd("java", "-Xmx256m", "-Dfile.encoding=UTF-8", "-classpath", "/app", JAVA_FILE_NAME)  //创建容器时执行命令
                .withHostConfig(hostConfig) //路径挂载与端口映射
                .withTty(false) // 配置 TTY 支持终端会话
                .withStdinOpen(true) // 启用标准输入
                .exec();
        String containerId = response.getId();
        // 2. 将文件复制到容器中
        String containerPath = "/app";  // 容器中的目标路径

        // 使用 Docker 的 cp 功能进行文件复制
        dockerClient.copyArchiveToContainerCmd(containerId)
                .withHostResource(localFilePath)
                .withRemotePath(containerPath)
                .exec();

        try {
            //3.启动容器，执行程序
            dockerClient.startContainerCmd(containerId).exec();
            // 启动 exec
            StopWatch stopWatch = new StopWatch();
            // 使用回调处理输出
            for (String inputCase : input) {
                // 创建 exec 命令
                //对应 docker exec -it <container_id> <command>
                final String[] outputCase = new String[1];
                final long[] time = {0};
                final long[] memory = {0};
                final boolean[] outTime = {true};
                StringBuilder errorMessage = new StringBuilder();
                // 获取容器的内存使用情况
                ResultCallback<Statistics> stat = dockerClient.statsCmd(containerId).exec(new ResultCallback<Statistics>() {
                    @Override
                    public void close() throws IOException {
                    }

                    @Override
                    public void onStart(Closeable closeable) {
                    }

                    @Override
                    public void onNext(Statistics stats) {
                        // 处理内存使用情况
                        if (stats.getMemoryStats() != null) {
                            long memoryUsage = stats.getMemoryStats().getUsage(); // 获取内存使用情况
                            memory[0] = Math.max(memoryUsage, memory[0]);
                            System.out.println("内存使用情况: " + memoryUsage + " KB");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

                ResultCallback.Adapter<Frame> callback = new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame frame) {
                        // 处理输出
                        StreamType streamType = frame.getStreamType();
                        String output = new String(frame.getPayload(), StandardCharsets.UTF_8);
                        // 仅当输出不为空时才打印
                        if (!output.trim().isEmpty()) {
                            if (StreamType.STDERR.equals(streamType)) {
                                System.err.print("标准错误: " + output);
                                errorMessage.append(output);
                            } else {
                                System.out.println("输出结果:" + output);
                                outputCase[0] = output.trim();
                                outTime[0] = false;
                            }
                        }
                    }
                };

                ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                        .withAttachStdout(true)
                        .withAttachStderr(true)
                        .withAttachStdin(true)
                        .withTty(false) // 使用 TTY
                        .withCmd("java", "-Xmx256m", "-Dfile.encoding=UTF-8", "-classpath", "./app", JAVA_FILE_NAME) // 指定执行的命令
                        .exec();
                String[] split = inputCase.split(" ");
                String join = StrUtil.join("\n", split) + "\n";
                ExecStartCmd execStartCmd = dockerClient.execStartCmd(execCreateCmdResponse.getId())
                        .withStdIn(new ByteArrayInputStream(join.getBytes(StandardCharsets.UTF_8)))
                        .withDetach(false) // 使其在前台运行
                        .withTty(false); // 使用 TTY
                if (!stopWatch.isRunning())
                    stopWatch.start();
                execStartCmd.exec(callback).awaitCompletion(2, TimeUnit.SECONDS);
                stopWatch.stop();
                time[0] = stopWatch.getLastTaskTimeMillis();

                ExecuteResp executeResp = new ExecuteResp();
                executeResp.setExecCode(0);
                executeResp.setOutput(outputCase[0]);
                executeResp.setTime(time[0]);
                executeResp.setMemory((int) memory[0]);

                if (outTime[0]) {
                    executeResp.setExecCode(1);
                    executeResp.setErrorMessage("run file out of time");
                    System.out.println("超时啦！");
                }
                if(!errorMessage.isEmpty()){
                    executeResp.setExecCode(2);
                    executeResp.setErrorMessage(errorMessage.toString());
                }
                executeRespList.add(executeResp);
                stat.close();
                System.out.println("exec结束");
            }

            // 获取容器的详细信息
            //检测容器状态
            InspectContainerResponse.ContainerState containerState = dockerClient.inspectContainerCmd(containerId)
                    .exec()
                    .getState();
            //停止容器
            if (containerState != null && Boolean.TRUE.equals(containerState.getRunning())) {
                dockerClient.stopContainerCmd(containerId).exec();
                System.out.println("容器停止");
            }
            //删除容器
            dockerClient.removeContainerCmd(containerId).exec();
            System.out.println("容器删除成功");

            return executeRespList;
        } catch (InterruptedException | IOException e) {
            ExecuteResp executeResp = new ExecuteResp();
            executeResp.setExecCode(3);
            executeResp.setErrorMessage(e.getMessage());
            return List.of(executeResp);
        }
    }
}
