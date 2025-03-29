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
    public PropertySource<?> dotenvPropertySource(ConfigurableEnvironment environment) {
        Dotenv dotenv = dotenv();
        Map<String, Object> properties = new HashMap<>();

        System.out.println("Loading environment variables from the .env file:");
        System.out.println("Path to the .env file: " + new File(".env").getAbsolutePath());
        System.out.println("Does the .env file exist: " + new File(".env").exists());

        for (DotenvEntry entry : dotenv.entries()) {
            properties.put(entry.getKey(), entry.getValue());
            System.out.println("Loaded variable: " + entry.getKey() + " = " + entry.getValue());
        }

        // Check specific database variables
        System.out.println("Checking database variables:");
        System.out.println("SPRING_DATASOURCE_URL: " + dotenv.get("SPRING_DATASOURCE_URL"));
        System.out.println("SPRING_DATASOURCE_USERNAME: " + dotenv.get("SPRING_DATASOURCE_USERNAME"));
        System.out.println("SPRING_DATASOURCE_PASSWORD: " + dotenv.get("SPRING_DATASOURCE_PASSWORD"));

        MapPropertySource propertySource = new MapPropertySource("dotenv", properties);
        environment.getPropertySources().addFirst(propertySource);

        return propertySource;
    }
}
