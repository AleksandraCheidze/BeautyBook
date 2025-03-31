package com.example.end.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private JwtKeyConfig access;
    private JwtKeyConfig refresh;

    public static class JwtKeyConfig {
        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public JwtKeyConfig getAccess() {
        return access;
    }

    public void setAccess(JwtKeyConfig access) {
        this.access = access;
    }

    public JwtKeyConfig getRefresh() {
        return refresh;
    }

    public void setRefresh(JwtKeyConfig refresh) {
        this.refresh = refresh;
    }
} 