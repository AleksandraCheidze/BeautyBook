package com.example.end;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;

public class RunEnvTest {
    public static void main(String[] args) {
        System.out.println("Current directory: " + new File(".").getAbsolutePath());
        System.out.println("Fail .env exist: " + new File(".env").exists());

        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .filename(".env")
                .ignoreIfMissing()
                .load();

    }
}