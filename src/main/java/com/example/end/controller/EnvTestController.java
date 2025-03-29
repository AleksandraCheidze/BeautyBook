package com.example.end.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Hidden
@RestController
@RequestMapping("/api/env-test")
public class EnvTestController {

    @Value("${CLOUDINARY_CLOUD_NAME:не найдено}")
    private String cloudName;

    @Value("${SPRING_MAIL_USERNAME:не найдено}")
    private String mailUsername;

    @Value("${SPRING_DATASOURCE_URL:не найдено}")
    private String dbUrl;

    @GetMapping
    public Map<String, String> testEnvVars() {
        Map<String, String> envVars = new HashMap<>();
        envVars.put("CLOUDINARY_CLOUD_NAME", cloudName);
        envVars.put("SPRING_MAIL_USERNAME", mailUsername);
        envVars.put("SPRING_DATASOURCE_URL", dbUrl);

        return envVars;
    }
}