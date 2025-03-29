package com.example.end;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.io.File;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        SecurityAutoConfiguration.class
})
@ComponentScan(basePackages = { "com.example.end.controller", "com.example.end.config" })
public class EnvTestApp {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .filename(".env")
                .ignoreIfMissing()
                .load();

        System.out.println("Текущая директория: " + new File(".").getAbsolutePath());
        System.out.println("Файл .env существует: " + new File(".env").exists());

        System.out.println("\nПеременные из .env файла:");
        for (var entry : dotenv.entries()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());

            System.setProperty(entry.getKey(), entry.getValue());
        }

        ConfigurableApplicationContext context = SpringApplication.run(EnvTestApp.class, args);

        Environment env = context.getEnvironment();
        System.out.println("\nПеременные из Environment:");
        System.out.println("ACCESS_KEY = " + env.getProperty("ACCESS_KEY"));
        System.out.println("CLOUDINARY_API_KEY = " + env.getProperty("CLOUDINARY_API_KEY"));
        System.out.println("SPRING_MAIL_USERNAME = " + env.getProperty("SPRING_MAIL_USERNAME"));
    }
}