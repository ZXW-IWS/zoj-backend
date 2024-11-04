package com.zuu.codesandbox.config;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.zuu.codesandbox.ZojCodeSandBoxApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/27 15:20
 */
@Configuration
@Slf4j
public class JavaDockerConfig {
    @Value("${docker.host}")
    public String host;
    @Value("${docker.port}")
    public String port;
    @Bean
    public DockerClient dockerClientConfig() throws IOException {
        String dockerHost = String.format("tcp://%s:%s",host,port);
        //String dockerCertPath = ZojCodeSandBoxApplication.class
        //        .getClassLoader()
        //        .getResource("tls-client-certs-docker")
        //        .getPath();
        //log.info("dockerHost:{}",dockerHost);
        //log.info("dockerPath:{}",dockerCertPath);
        // 读取证书文件，假设证书文件名为 ca.pem, cert.pem 和 key.pem
        InputStream caCertInputStream = getClass().getClassLoader().getResourceAsStream("tls-client-certs-docker/ca.pem");
        InputStream certInputStream = getClass().getClassLoader().getResourceAsStream("tls-client-certs-docker/cert.pem");
        InputStream keyInputStream = getClass().getClassLoader().getResourceAsStream("tls-client-certs-docker/key.pem");

        // 将输入流写入临时文件或直接使用其他方式
        Path tempDir = Files.createTempDirectory("dockerCerts");
        Files.copy(caCertInputStream, tempDir.resolve("ca.pem"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(certInputStream, tempDir.resolve("cert.pem"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(keyInputStream, tempDir.resolve("key.pem"), StandardCopyOption.REPLACE_EXISTING);
        String dockerCertPath = tempDir.toString(); // 临时目录路径

        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .withDockerTlsVerify(true)
                .withDockerCertPath(dockerCertPath)
                .build();
        ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        return DockerClientBuilder.getInstance(config)
                .withDockerHttpClient(httpClient)
                .build();
    }
}
