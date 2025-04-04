package com.example.end.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class EnvConfig {

    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure()
                .directory(".")
                .filename(".env")
                .ignoreIfMissing()
                .load();
    }

    @Bean
    public PropertySource<?> dotenvPropertySource(ConfigurableEnvironment environment, Dotenv dotenv) {
        Map<String, Object> properties = new HashMap<>();

        for (DotenvEntry entry : dotenv.entries()) {
            properties.put(entry.getKey(), entry.getValue());
            System.out.println("Loaded variable: " + entry.getKey());
        }

        MapPropertySource propertySource = new MapPropertySource("dotenv", properties);
        environment.getPropertySources().addFirst(propertySource);

        return propertySource;
    }
}