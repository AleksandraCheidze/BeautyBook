package com.example.end;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class BeautyProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeautyProjectApplication.class, args);
	}
}
