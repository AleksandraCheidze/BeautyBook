package com.example.end.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Разрешить любые пути
                .allowedOrigins("*") // Разрешить запросы с любых доменов
                .allowedMethods("*") // Разрешить любые HTTP методы
                .allowedHeaders("*"); // Разрешить любые заголовки
    }
}