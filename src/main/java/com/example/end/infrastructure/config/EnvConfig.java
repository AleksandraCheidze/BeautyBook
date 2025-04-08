package com.example.end.infrastructure.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Paths;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EnvConfig {
    private static final Logger logger = LoggerFactory.getLogger(EnvConfig.class);

    static {
        loadEnv();
    }

    private static void loadEnv() {
        String currentDir = System.getProperty("user.dir");
        logger.info("Current directory: {}", currentDir);

        String[] possiblePaths = {
            ".",
            currentDir,
            Paths.get(currentDir).getParent().toString(),
            System.getProperty("user.home")
        };

        File envFile = null;
        for (String path : possiblePaths) {
            File testFile = new File(path, ".env");
            logger.info("Checking .env at: {}", testFile.getAbsolutePath());
            if (testFile.exists()) {
                envFile = testFile;
                logger.info("Found .env file at: {}", testFile.getAbsolutePath());
                break;
            }
        }

        if (envFile == null) {
            logger.warn("No .env file found in any of the searched locations");
            return;
        }

        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(envFile.getParent())
                    .filename(".env")
                    .load();
            dotenv.entries().forEach(entry -> {
                if (System.getProperty(entry.getKey()) == null && 
                    System.getenv(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                    logger.debug("Set environment variable: {}", entry.getKey());
                }
            });

        } catch (Exception e) {
            logger.error("Error loading .env file: {}", e.getMessage(), e);
        }
    }

    @PostConstruct
    public void init() {
        logger.info("EnvConfig initialized");
    }
}