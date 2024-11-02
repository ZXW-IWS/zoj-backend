package com.zuu.codesandbox.config;

import cn.hutool.core.io.resource.ResourceUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/27 15:20
 */
@Configuration
public class JavaDockerConfig {
    @Value("${docker.host}")
    public String host;
    @Value("${docker.port}")
    public String port;
    @Bean
    public DockerClient dockerClientConfig() throws FileNotFoundException {
        String dockerHost = String.format("tcp://%s:%s",host,port);
        String dockerCertPath = ResourceUtils.getURL("classpath:").getPath()
                + File.separator
                + "tls-client-certs-docker";
        //String dockerCertPath = System.getProperty("user.dir") + File.separator
        //        + "src" + File.separator
        //        + "main" + File.separator
        //        + "resources" + File.separator
        //        + "tls-client-certs-docker";
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
