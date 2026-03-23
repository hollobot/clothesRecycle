package com.example.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 跨域配置属性。
 */
@Data
@Component
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    /**
     * 允许的源列表。支持 "*"。
     */
    private List<String> allowedOrigins = new ArrayList<>(List.of("*"));

    /**
     * 允许的方法列表。
     */
    private List<String> allowedMethods = new ArrayList<>(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

    /**
     * 允许的请求头列表。
     */
    private List<String> allowedHeaders = new ArrayList<>(List.of("Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin"));

    /**
     * 暴露给前端的响应头列表。
     */
    private List<String> exposedHeaders = new ArrayList<>(List.of("Authorization"));

    /**
     * 预检请求缓存时间（秒）。
     */
    private Long maxAge = 3600L;

    /**
     * 是否允许携带凭证（Cookie/Authorization）。
     */
    private Boolean allowCredentials = false;
}
