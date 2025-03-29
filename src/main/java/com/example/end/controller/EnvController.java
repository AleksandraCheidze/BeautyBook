package com.example.end.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Hidden
@RestController
@RequestMapping("/api/env")
public class EnvController {

    private final Environment environment;

    @Autowired
    public EnvController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping
    public Map<String, String> getEnvVariables() {
        Map<String, String> envVars = new HashMap<>();

        envVars.put("CLOUDINARY_CLOUD_NAME", environment.getProperty("CLOUDINARY_CLOUD_NAME"));
        envVars.put("CLOUDINARY_API_KEY", environment.getProperty("CLOUDINARY_API_KEY"));
        envVars.put("ACCESS_KEY", environment.getProperty("ACCESS_KEY"));
        envVars.put("SPRING_MAIL_USERNAME", environment.getProperty("SPRING_MAIL_USERNAME"));

        return envVars;
    }
}