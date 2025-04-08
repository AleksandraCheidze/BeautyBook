package com.example.end;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class BeautyProjectApplication {
	private static final Logger logger = LoggerFactory.getLogger(BeautyProjectApplication.class);

	public static void main(String[] args) {
		logger.info("Starting BeautyProjectApplication");
		SpringApplication.run(BeautyProjectApplication.class, args);
		logger.info("BeautyProjectApplication started successfully");
	}
}