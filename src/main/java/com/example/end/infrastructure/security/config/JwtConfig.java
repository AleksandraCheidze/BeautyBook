package com.example.end.infrastructure.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    
    @Value("${jwt.access.key}")
    private String accessKey;
    
    @Value("${jwt.refresh.key}")
    private String refreshKey;
    
    public String getAccessKey() {
        return accessKey;
    }
    
    public String getRefreshKey() {
        return refreshKey;
    }
} 