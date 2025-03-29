package com.example.end;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvTest {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        System.out.println("Проверка переменных окружения из .env файла:");
        System.out.println("ACCESS_KEY: " + dotenv.get("ACCESS_KEY"));
        System.out.println("CLOUDINARY_API_KEY: " + dotenv.get("CLOUDINARY_API_KEY"));
        System.out.println("CLOUDINARY_API_SECRET: " + dotenv.get("CLOUDINARY_API_SECRET"));
        System.out.println("CLOUDINARY_CLOUD_NAME: " + dotenv.get("CLOUDINARY_CLOUD_NAME"));
        System.out.println("REFRESH_KEY: " + dotenv.get("REFRESH_KEY"));
        System.out.println("SPRING_DATASOURCE_PASSWORD: " + dotenv.get("SPRING_DATASOURCE_PASSWORD"));
        System.out.println("SPRING_DATASOURCE_URL: " + dotenv.get("SPRING_DATASOURCE_URL"));
        System.out.println("SPRING_DATASOURCE_USERNAME: " + dotenv.get("SPRING_DATASOURCE_USERNAME"));
        System.out.println("SPRING_MAIL_PASSWORD: " + dotenv.get("SPRING_MAIL_PASSWORD"));
        System.out.println("SPRING_MAIL_USERNAME: " + dotenv.get("SPRING_MAIL_USERNAME"));
    }
}