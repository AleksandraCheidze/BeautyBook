package com.example.end;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DotenvTest {

    @Test
    public void testDotenvLoading() {
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .ignoreIfMissing()
                .load();

        System.out.println("Проверка переменных окружения из .env файла:");
        System.out.println("ACCESS_KEY: " + dotenv.get("ACCESS_KEY"));
        System.out.println("CLOUDINARY_API_KEY: " + dotenv.get("CLOUDINARY_API_KEY"));
        System.out.println("CLOUDINARY_CLOUD_NAME: " + dotenv.get("CLOUDINARY_CLOUD_NAME"));

        assertNotNull(dotenv.get("ACCESS_KEY"), "ACCESS_KEY должен быть загружен");
        assertNotNull(dotenv.get("CLOUDINARY_API_KEY"), "CLOUDINARY_API_KEY должен быть загружен");
    }
}