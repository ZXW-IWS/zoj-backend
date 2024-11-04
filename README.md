# OJ项目后端源码

## (一) 数据库表

### 1. 用户表

```sql
DROP TABLE IF EXISTS user;
create table user
(
    id            bigint auto_increment comment '主键id'
        primary key,
    username      varchar(256)                        null comment '用户昵称',
    gender        varchar(16)                         null comment '用户性别 保密 男 女',
    user_password varchar(256)                        null comment '用户密码',
    avatar_url    varchar(512)                        null comment '用户头像的url地址',
    user_profile  varchar(512)                        null comment '用户简介信息',
    email         varchar(256)                        null comment '用户邮箱',
    user_status   tinyint   default 0                 not null comment '用户状态 0-正常',
    user_role     tinyint   default 0                 not null comment '用户权限  0-普通用户  1-管理员',
    create_time   timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint   default 0                 not null comment ' 0-未删除'
);
```

### 2. 题目表

题目提交数、通过数，

判题配置通过judgeConfig进行存储（json）：时间限制、空间限制、输入用例、输出用例

判题配置judgeConfig

```json
{
  "timeLimit":1000,
  "memoryLimit": 1000
}
```

判题用例judgeCase

```json
[
  {
    "input": x x,
    "output": x x
  },
  {
    "input": x x,
    "output": x x
  }
]
```
```sql
DROP TABLE IF EXISTS question;
CREATE TABLE question
(
    id bigint auto_increment comment '主键id'
        primary key,
    user_id         BIGINT                        not null comment '创建用户id',
    title           VARCHAR(256)                  null comment '题目标题',
    description     VARCHAR(512)                  null comment '题目描述',
    submit_count    INT         default 0         not null comment '题目提交数',
    accept_count    INT         default 0         not null comment '题目通过数',
    difficult       INT                           null comment '题目难度 0-简单 1-中等 2-困难',
    answer          TEXT                          null comment '题目答案',
    judge_config    TEXT                          null comment '判题配置（JSON),时间空间...',
    judge_case      TEXT                          null comment '判题用例(JSON)',
    create_time   timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint   default 0                 not null comment ' 0-未删除',
    INDEX idx_uid (user_id)
);
```

### 3. 标签表

```sql
DROP TABLE IF EXISTS tag;
CREATE TABLE tag
(
    id bigint auto_increment comment '主键id'
        primary key,
    name                    VARCHAR(128)        null comment '标签名',
    create_time   timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint   default 0                 not null comment ' 0-未删除'
);
```

### 4. 题目-标签表

```sql
DROP TABLE IF EXISTS question_tag;
CREATE TABLE question_tag
(
    id bigint auto_increment comment '主键id'
        primary key,
    question_id             BIGINT              not null comment '题目id',
    tag_id                  BIGINT              not null comment '标签id',
    create_time   timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint   default 0                 not null comment ' 0-未删除',
    INDEX idx_question_id (question_id),
    INDEX idx_tag_id (tag_id)
);
```

### 5. 题目提交表

uid、qid、语言、

```sql
DROP TABLE IF EXISTS question_submit;
CREATE TABLE question_submit
(
    id bigint auto_increment comment '主键id'
        primary key,
    user_id         BIGINT                        not null comment '用户id',
    question_id     BIGINT                        not null comment '题目id',
    language        VARCHAR(128)                  not null comment '语言',
    code            TEXT                          not null comment '代码',
    status          INT         default 0         not null comment '状态 0-待判题 1-判题中 2-成功 3-失败',
    judge_info      TEXT                          null comment '判题信息JSON(时间、空间)',
    create_time   timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint   default 0                 not null comment ' 0-未删除',
    INDEX idx_user_id (user_id),
    INDEX idx_question_id (question_id)
);
```











## (二) 判题机架构设计

判题机架构共分为判题模块以及代码沙箱模块；其中：

1. 判题模块负责接收代码并将代码发送给代码沙箱，并根据代码沙箱的执行结果来进行判题
2. 代码沙箱模块只负责编译运行代码并返回代码结果，不负责具体的判题（沙箱可以服用为代码执行器）

![img](https://cdn.nlark.com/yuque/0/2024/jpeg/43296583/1729664229140-98a6b59b-07fe-421b-bdb0-65e03d039f94.jpeg)

### 1. 代码沙箱架构设计

![img](https://cdn.nlark.com/yuque/0/2024/jpeg/43296583/1729664763842-722eeb93-b716-4703-94ca-103544f4d7b8.jpeg)

代码沙箱共用到了三种设计模式：策略模式、工厂模式以及代理模式。



## (三) Java原生代码沙箱实现

### 1. 命令行编译运行java程序

#### 一、编译程序

通过javac命令来编译java程序代码。在编译成功之后会生成一个.class文件

```bash
1. javac  源文件  
   直接编译源文件，并将编译后的.class文件放到与源文件统一目录下；
2. javac  -d   编译文件输出路径  源文件   
   编译源文件，并将编译后的 .class 文件 放到 -d 参数后的目标路径中；
         
3. javac  -d   编译文件输出路径  -encoding utf-8 源文件    : 
   编译文件,并将编译后的.class文件放到-d参数后的目标路径中，且指定编码格式 为utf-8,解决运行时中文乱码问题。
```

#### 二、运行程序

```bash
1. java class文件(不加.class后缀) A B ...
    运行程序,并向args数组传入值为A B ...
2. java -D'file.encoding=UTF-8'
    运行程序并将编码设为utf-8
```

### 2. java代码实现编译运行

#### 一、保存代码文件

1. 获取项目根目录绝对路径

```java
System.getProperty("user.dir"))
```

1. 将代码字符串写入文件并保存

```java
String rootPath = System.getProperty("user.dir") + File.separator + CODE_DIR_PATH;
String userPath = rootPath + File.separator + UUID.randomUUID();
String filePath = userPath + File.separator + JAVA_FILE_NAME;
File file = FileUtil.writeString(code,filePath,StandardCharsets.UTF_8);
```

#### 二、执行命令工具类

编写一个工具，用于执行命令并返回结果（这样编译程序和运行程序就不必重复编写代码了）

```java
public class ProcessUtils {

    public static ExecuteRes runCommand(String command, String optName, String arg){
        ExecuteRes executeRes = new ExecuteRes();
        String line;
        try {
            Process process = Runtime.getRuntime().exec(command);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            PrintWriter printWriter = new PrintWriter(process.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = process.errorReader();
            //程序输入
            if(StrUtil.isNotBlank(arg)){
                String[] split = arg.split(" ");
                String join = StrUtil.join("\n", split) + "\n";
                printWriter.print(join);
                printWriter.flush();
            }
            //执行命令，获取成功/错误信息
            int execCode = process.waitFor();
            StringBuilder message = new StringBuilder();
            if(execCode == 0){
                System.out.println(optName + "成功");
                while((line = bufferedReader.readLine()) != null){
                    message.append(line);
                }
            }else{
                System.out.println(optName + "失败,错误码:" + execCode);
                while((line = errorReader.readLine()) != null){
                    message.append(line);
                }
            }
            stopWatch.stop();
            System.out.println(message);
            executeRes.setExecCode(execCode);
            executeRes.setMessage(message.toString());
            executeRes.setOutput(message.toString());
            executeRes.setTime(stopWatch.getTotalTimeMillis());

            //关闭，防止卡死
            printWriter.close();
            bufferedReader.close();
            errorReader.close();
            return executeRes;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
```

#### 三、编译代码

```java
String compileCommand = String.format("javac -encoding utf-8 %s",file.getAbsolutePath());
ProcessUtils.runCommand(compileCommand,"编译",null);
```

#### 四、运行代码获取结果

```java
String runCommand = String.format("java -D'file.encoding=UTF-8' %s %s",filePath,JAVA_FILE_NAME);
List<String> output = new ArrayList<>();
long maxTime = 0;
for (String inputCase : input) {
    ExecuteRes executeRes = ProcessUtils.runCommand(runCommand, "运行", inputCase);
    output.add(executeRes.getOutput());
    maxTime = Math.max(maxTime,executeRes.getTime());
}
```

#### 五、删除文件

```java
if(file.getParentFile() != null){
    FileUtil.del(userPath);
}
```

#### 六、返回结果

```java
ExecuteCodeResp executeCodeResp = new ExecuteCodeResp();
JudgeInfo judgeInfo = new JudgeInfo();
judgeInfo.setTime((int) maxTime);
executeCodeResp.setOutput(output);
executeCodeResp.setJudgeInfo(judgeInfo);
executeCodeResp.setMessage("ok");
executeCodeResp.setStatus(0);

return executeCodeResp;
```



## (四) java原生代码沙箱安全问题

### 1. 占用资源不释放（sleep）

```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(60 * 60 * 1000);
        System.out.println("我睡醒啦");
    }
}
```

### 2. 占用空间不释放

```java
public class Main {
    public static void main(String[] args) {
        List<byte[]> bytes = new ArrayList<>();
        while(true){
            bytes.add(new byte[999999]);
        }
    }
}
```

### 3. 读取服务器文件信息

```java
public class Main {
    public static void main(String[] args) throws IOException {
        String rootPath = System.getProperty("user.dir");
        String resourcePath = "src/main/resources/application.yaml";
        List<String> allLines = Files.readAllLines(Paths.get(rootPath + File.separator + resourcePath));
        for (String line : allLines) {
            System.out.println(line);
        }
    }
}
```

### 4. 向服务器写入木马文件

```java
public class Main {
    public static void main(String[] args) throws IOException {
        String rootPath = System.getProperty("user.dir");
        String filePath = "src/main/resources" + File.separator + "木马文件.sh";
        String muma = "java --version";
        Files.write(Path.of(filePath),muma.getBytes());
        System.out.println("木马程序写入，你完了哈哈哈");
    }
}
```

### 5. 运行服务器程序（如木马）

```java
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String rootPath = System.getProperty("user.dir");
        String filePath = "src/main/resources/木马文件.sh";
        String command = String.format("sh %s",rootPath + File.separator + filePath);
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder sb = new StringBuilder();
        while((line = bufferedReader.readLine()) != null){
            sb.append(line + "\n");
        }
        System.out.println(sb);
    }
}
```

### 6. 执行系统自带的危险命令

```java
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String command = "java --version";
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder sb = new StringBuilder();
        while((line = bufferedReader.readLine()) != null){
            sb.append(line + "\n");
        }
        System.out.println(sb);
    }
}
```

## (五) 安全问题解决方案

### 1. 超时控制

通过创建守护线程，若线程睡眠结束程序尚未执行完，则直接杀死进程。

```java
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
if (maxTime > 0) deamonThread.start();
```

### 2. 资源限制

在通过java命令运行class文件时，可以指定程序所能够占用的堆内存最大/最小值，通过此防止程序占用过多的内存空间。-Xmx参数可设置最大堆内存，如-Xmx256m

```java
String runCommand = String.format("java -Xmx256m -D'file.encoding=UTF-8' %s %s",filePath,JAVA_FILE_NAME);
```

### 3. 限制代码段

通过字段树来防止用户代码中存在有危险的代码段。

### 4. 安全管理器

可以使用安全管理器来设置程序运行时能够拥有的权限。

但该方式已过时，原因为：

1. 需要配置的环境太过复杂，粒度太细。
2. 主要用于沙箱环境，但现在大多数沙箱都使用的是容器化技术或虚拟化技术，因此安全管理器使用场景小。
3. 使用复杂，难以维护。



### 5. 代码环境隔离

直接通过容器化技术，在容器中执行用户代码，便能够成功隔离用户代码执行环境与服务器主机所在的环境。

## (六) Java操作Docker

使用docker-java库

### 1. 引入依赖

```xml
<!-- https://mvnrepository.com/artifact/com.github.docker-java/docker-java -->
<dependency>
  <groupId>com.github.docker-java</groupId>
  <artifactId>docker-java</artifactId>
  <version>3.3.3</version>
</dependency>
<!-- https://mvnrepository.com/artifact/com.github.docker-java/docker-java-transport-httpclient5 -->
<dependency>
  <groupId>com.github.docker-java</groupId>
  <artifactId>docker-java-transport-httpclient5</artifactId>
  <version>3.3.3</version>
</dependency>
<dependency>
  <groupId>org.apache.httpcomponents.client5</groupId>
  <artifactId>httpclient5</artifactId>
  <version>5.3.1</version>
</dependency>
```

### 2. docker连接初始化(无tsl加密)

```java
ApacheDockerHttpClient dockerHttpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(URI.create("tcp://182.92.80.155:2375"))  // 替换为你的 Docker 主机地址
                .sslConfig(null)
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
DockerClient dockerClient = DockerClientBuilder
        .getInstance()
        .withDockerHttpClient(dockerHttpClient)
        .build();
//获取info
dockerClient.pingCmd().exec();
Info info = dockerClient.infoCmd().exec();
System.out.println(JSONUtil.toJsonStr(info));
```

### 3. 服务器docker配置tls安全连接

#### 一、自动生成tls证书脚本

将该脚本文件保存到sh文件中

```bash
#!/bin/bash
#
# -------------------------------------------------------------
# 自动创建 Docker TLS 证书
# -------------------------------------------------------------
# 以下是配置信息
# --[BEGIN]------------------------------
CODE="docker"
IP="182.92.80.155"
PASSWORD="123456"
COUNTRY="CN"
STATE="SHANNXI"
CITY="XIAN"
ORGANIZATION="thyc"
ORGANIZATIONAL_UNIT="Dev"
COMMON_NAME="$IP"
EMAIL="1871309907@qq.com"
# --[END]--
# Generate CA key
openssl genrsa -aes256 -passout "pass:$PASSWORD" -out "ca-key-$CODE.pem" 4096
# Generate CA
openssl req -new -x509 -days 365 -key "ca-key-$CODE.pem" -sha256 -out "ca-$CODE.pem" -passin "pass:$PASSWORD" -subj "/C=$COUNTRY/ST=$STATE/L=$CITY/O=$ORGANIZATION/OU=$ORGANIZATIONAL_UNIT/CN=$COMMON_NAME/emailAddress=$EMAIL"
# Generate Server key
openssl genrsa -out "server-key-$CODE.pem" 4096
# Generate Server Certs.
openssl req -subj "/CN=$COMMON_NAME" -sha256 -new -key "server-key-$CODE.pem" -out server.csr
echo "subjectAltName = IP:$IP,IP:127.0.0.1" >> extfile.cnf
echo "extendedKeyUsage = serverAuth" >> extfile.cnf
openssl x509 -req -days 365 -sha256 -in server.csr -passin "pass:$PASSWORD" -CA "ca-$CODE.pem" -CAkey "ca-key-$CODE.pem" -CAcreateserial -out "server-cert-$CODE.pem" -extfile extfile.cnf
# Generate Client Certs.
rm -f extfile.cnf
openssl genrsa -out "key-$CODE.pem" 4096
openssl req -subj '/CN=client' -new -key "key-$CODE.pem" -out client.csr
echo extendedKeyUsage = clientAuth >> extfile.cnf
openssl x509 -req -days 365 -sha256 -in client.csr -passin "pass:$PASSWORD" -CA "ca-$CODE.pem" -CAkey "ca-key-$CODE.pem" -CAcreateserial -out "cert-$CODE.pem" -extfile extfile.cnf
rm -vf client.csr server.csr
chmod -v 0400 "ca-key-$CODE.pem" "key-$CODE.pem" "server-key-$CODE.pem"
chmod -v 0444 "ca-$CODE.pem" "server-cert-$CODE.pem" "cert-$CODE.pem"
# 打包客户端证书
mkdir -p "tls-client-certs-$CODE"
cp -f "ca-$CODE.pem" "cert-$CODE.pem" "key-$CODE.pem" "tls-client-certs-$CODE/"
cd "tls-client-certs-$CODE"
tar zcf "tls-client-certs-$CODE.tar.gz" *
mv "tls-client-certs-$CODE.tar.gz" ../
cd ..
rm -rf "tls-client-certs-$CODE"
# 拷贝服务端证书
mkdir -p /etc/docker/certs.d
cp "ca-$CODE.pem" "server-cert-$CODE.pem" "server-key-$CODE.pem" /etc/docker/certs.d/
```

#### 二、执行脚本

```bash
chmod a+x auto_gen_docker.sh
sh auto_gen_docker.sh
```

#### 三、docker配置文件修改

```bash
#配置文件地址
vim /lib/systemd/system/docker.service

#修改如下代码
ExecStart=/usr/bin/dockerd -H tcp://127.0.0.1:2375 -H unix://var/run/docker.sock --tlsverify --tlscacert=/etc/docker/certs.d/ca-docker.pem --tlscert=/etc/docker/certs.d/server-cert-docker.pem --tlskey=/etc/docker/certs.d/server-key-docker.pem

#修改后执行
systemctl daemon-reload
service docker restart
```

#### 四、拷贝当前目录下的tls-client-certs-docker.tar 文件到项目的resource下，并解压

### 4. docker连接初始化(有tls加密

```java
DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
.withDockerHost("tcp://182.92.80.155:2375")
.withDockerTlsVerify(true)
.withDockerCertPath("/Users/zuu/MyProjects/OJ/zoj-codesandbox/src/main/resources/tls-client-certs-docker")
.build();

ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
.dockerHost(config.getDockerHost())
.sslConfig(config.getSSLConfig())
.maxConnections(100)
.connectionTimeout(Duration.ofSeconds(30))
.responseTimeout(Duration.ofSeconds(45))
.build();

DockerClient dockerClient = DockerClientBuilder.getInstance(config)
.withDockerHttpClient(httpClient)
.build();
//获取info
dockerClient.pingCmd().exec();
Info info = dockerClient.infoCmd().exec();
System.out.println(JSONUtil.toJsonStr(info));
```

### 5. 拉取镜像

```java
//拉取镜像
String ghost = "openjdk:17.0";
PullImageCmd pullImageCmd = dockerClient.pullImageCmd(ghost);
//获取镜像拉取过程中的回调信息
PullImageResultCallback pullImageResultCallback = new PullImageResultCallback(){
    @Override
    public void onNext(PullResponseItem item) {
        System.out.println("拉取镜像中:" + item);
    }
};
pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
System.out.println("镜像拉取完成");
```

### 6. 创建容器、端口映射、路径挂载

```java
//创建容器
//映射端口8088—>80
ExposedPort tcp80 = ExposedPort.tcp(80);
Ports portBindings = new Ports();
portBindings.bind(tcp80, Ports.Binding.bindPort(8088));

HostConfig hostConfig = HostConfig.newHostConfig();
Bind bind = new Bind("服务器路径",new Volume("容器路径"));
hostConfig.setBinds(bind);
hostConfig.withPortBindings(portBindings);
CreateContainerResponse response = dockerClient
        .createContainerCmd(ghost)
        //.withCmd("命令")  //创建容器时执行命令
        //.withHostConfig(hostConfig) //路径挂载与端口映射
        .withTty(true) // 配置 TTY 支持终端会话
        .withStdinOpen(true) // 启用标准输入
        .exec();
System.out.println("容器id:" + response.getId());
```

### 7. 删除容器

```java
dockerClient.removeContainerCmd(containerId).exec();
System.out.println("容器删除成功");
```

### 8. 删除镜像

```java
dockerClient.removeImageCmd(ghost).exec();
System.out.println("镜像删除成功");
```

### 9. 启动容器

```java
dockerClient.startContainerCmd(containerId).exec();
System.out.println("容器启动成功");
```

### 10. 检测容器状态

```java
//检测容器状态
InspectContainerResponse.ContainerState containerState = dockerClient.inspectContainerCmd(containerId)
.exec()
.getState();
System.out.println(containerState);
```

### 11. 停止容器

```java
if(containerState != null && Boolean.TRUE.equals(containerState.getRunning())){
    dockerClient.stopContainerCmd(containerId).exec();
    System.out.println("容器停止成功");
}
```

### 12. 获取容器执行日志

```java
//获取容器执行日志
LogContainerCmd logContainerCmd = dockerClient.logContainerCmd(containerId)
.withStdOut(true)         // 启用标准输出
.withStdErr(true)         // 启用错误输出
.withFollowStream(true);  // 持续监听日志
logContainerCmd.exec(new ResultCallback.Adapter<Frame>() {
    @Override
    public void onNext(Frame frame) {
        // 处理每个日志帧的数据
        System.out.println(new String(frame.getPayload()));
    }
})
.awaitCompletion();
```







## (七) Docker实现代码沙箱

### 1. 编译代码文件

```java
//保存java代码文件
String rootPath = System.getProperty("user.dir") + File.separator + CODE_DIR_PATH;
String userPath = rootPath + File.separator + UUID.randomUUID();
String filePath = userPath + File.separator + JAVA_FILE_NAME + JAVA_FILE_SUFFIX;
File file = FileUtil.writeString(code, filePath, StandardCharsets.UTF_8);
//编译
String compileCommand = String.format("javac -encoding utf-8 %s", file.getAbsolutePath());
ProcessUtils.runCommand(compileCommand, "编译", null);
```

### 2. 创建容器，指定内存、网络等限制

```java
//创建docker容器并将class文件挂载上去，并运行
DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
.withDockerHost("tcp://182.92.80.155:2375")
.withDockerTlsVerify(true)
.withDockerCertPath("/Users/zuu/MyProjects/OJ/zoj-codesandbox/src/main/resources/tls-client-certs-docker")
.build();

ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
.dockerHost(config.getDockerHost())
.sslConfig(config.getSSLConfig())
.maxConnections(100)
.connectionTimeout(Duration.ofSeconds(30))
.responseTimeout(Duration.ofSeconds(45))
.build();

DockerClient dockerClient = DockerClientBuilder.getInstance(config)
.withDockerHttpClient(httpClient)
.build();
String localFilePath = userPath + File.separator + JAVA_FILE_NAME + CLASS_SUFFIX;  // 主机上的文件路径
String runCommand = String.format("java -Xmx256m -D'file.encoding=UTF-8' %s %s", "/app", JAVA_FILE_NAME);
HostConfig hostConfig = HostConfig.newHostConfig();
Bind bind = new Bind(localFilePath, new Volume("/app"));
hostConfig.setBinds(bind);
//创建容器
CreateContainerResponse response = dockerClient
.createContainerCmd(GHOST)
.withMemory(1024*1024*100L) //内存限制(KB)
.withNetworkDisabled(true) //禁止使用网络
.withStopTimeout(30) //设置停止超时时间为30秒
.withCmd("java", "-Xmx256m", "-Dfile.encoding=UTF-8", "-classpath", "/app", JAVA_FILE_NAME)  //创建容器时执行命令
.withHostConfig(hostConfig) //路径挂载与端口映射
.withTty(false) // 配置 TTY 支持终端会话
.withStdinOpen(true) // 启用标准输入
.exec();
String containerId = response.getId();
```

### 3. 复制class文件到容器中

```java
// 2. 将文件复制到容器中
String containerPath = "/app";  // 容器中的目标路径

// 使用 Docker 的 cp 功能进行文件复制
dockerClient.copyArchiveToContainerCmd(containerId)
    .withHostResource(localFilePath)
    .withRemotePath(containerPath)
    .exec();
```



### 4. 启动容器

```java
dockerClient.startContainerCmd(containerId).exec();
```

### 5. 创建exec -it命令并向容器传入参数并获取输出

```java
// 启动 exec
            List<String> outputList = new ArrayList<>();
            List<Long> timeList = new ArrayList<>();
            List<Long> memoryList = new ArrayList<>();
            StopWatch stopWatch = new StopWatch();
            // 使用回调处理输出
            for (String inputCase : input) {
                // 创建 exec 命令
                //对应 docker exec -it <container_id> <command>
                final boolean[] outTime = {true};
                ResultCallback.Adapter<Frame> callback = new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame frame) {
                        // 处理输出
                        StreamType streamType = frame.getStreamType();
                        String output = new String(frame.getPayload(), StandardCharsets.UTF_8);

                        // 仅当输出不为空时才打印
                        if (!output.trim().isEmpty()) {
                            if(StreamType.STDERR.equals(streamType)){
                                System.err.print("标准错误: " + output);
                            }else{
                                System.out.println("流类型" + streamType);
                                System.out.println(output);
                                outputList.add(output.trim());
                                stopWatch.stop();
                                timeList.add(stopWatch.getLastTaskTimeMillis());
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
                        .withCmd("java", "-Xmx256m", "-Dfile.encoding=UTF-8","-classpath", "./app", JAVA_FILE_NAME) // 指定执行的命令
                        .exec();
                String[] split = inputCase.split(" ");
                String join = StrUtil.join("\n", split) + "\n";
                ExecStartCmd execStartCmd = dockerClient.execStartCmd(execCreateCmdResponse.getId())
                        .withStdIn(new ByteArrayInputStream(join.getBytes(StandardCharsets.UTF_8)))
                        .withDetach(false) // 使其在前台运行
                        .withTty(false); // 使用 TTY
                if(!stopWatch.isRunning())
                    stopWatch.start();
                execStartCmd.exec(callback).awaitCompletion(2, TimeUnit.SECONDS);
                if(outTime[0]){
                    System.out.println("超时啦！");
                }
                System.out.println("exec结束");
            }
```



### 6. 停止容器

```java
//检测容器状态
InspectContainerResponse.ContainerState containerState = dockerClient.inspectContainerCmd(containerId)
        .exec()
        .getState();
//停止容器
if (containerState != null && Boolean.TRUE.equals(containerState.getRunning())) {
    dockerClient.stopContainerCmd(containerId).exec();
    System.out.println("容器停止");
}
```

### 7. 删除容器

```java
dockerClient.removeContainerCmd(containerId).exec();
System.out.println("容器删除成功");
```

### 8. 删除本地文件

```java
if (file.getParentFile() != null) {
    FileUtil.del(userPath);
}
```

### 9. 获取内存限制

```java
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
            System.out.println(stats.getMemoryStats());
            long memoryUsage = stats.getMemoryStats().getUsage(); // 获取内存使用情况
            memoryList.add(memoryUsage);
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
```

### 10. 如何获取程序是否超时

通过一个布尔变量来判断，初始化为true,若程序执行成功并返回结果到达回调函数，则将其值设为false即可。代码见上面第5步。



### 11. 模版方法设计模式改造沙箱代码