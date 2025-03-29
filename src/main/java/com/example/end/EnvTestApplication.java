package com.example.end;

import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Hidden
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class })
@RestController
public class EnvTestApplication {

    private final Dotenv dotenv;

    public EnvTestApplication() {
        this.dotenv = Dotenv.configure().ignoreIfMissing().load();
    }

    public static void main(String[] args) {
        SpringApplication.run(EnvTestApplication.class, args);
    }

    @GetMapping("/test-env")
    public Map<String, String> testEnv() {
        Map<String, String> envVars = new HashMap<>();

        envVars.put("ACCESS_KEY", dotenv.get("ACCESS_KEY"));
        envVars.put("CLOUDINARY_API_KEY", dotenv.get("CLOUDINARY_API_KEY"));
        envVars.put("CLOUDINARY_API_SECRET", dotenv.get("CLOUDINARY_API_SECRET"));
        envVars.put("CLOUDINARY_CLOUD_NAME", dotenv.get("CLOUDINARY_CLOUD_NAME"));
        envVars.put("REFRESH_KEY", dotenv.get("REFRESH_KEY"));
        envVars.put("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
        envVars.put("SPRING_DATASOURCE_URL", dotenv.get("SPRING_DATASOURCE_URL"));
        envVars.put("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
        envVars.put("SPRING_MAIL_PASSWORD", dotenv.get("SPRING_MAIL_PASSWORD"));
        envVars.put("SPRING_MAIL_USERNAME", dotenv.get("SPRING_MAIL_USERNAME"));

        return envVars;
    }
}