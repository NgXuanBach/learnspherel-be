package com.learnspherel.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.servlet.multipart")
@Getter
@Setter
public class FileStorageProperties {
    private String maxFileSize;
    private String maxRequestSize;
}