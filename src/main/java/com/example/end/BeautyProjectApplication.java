package com.example.end;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class BeautyProjectApplication {



	public static void main(String[] args) {
		System.out.println("Database URL: " + System.getenv("PROD_DB_HOST"));
		System.out.println("Database Port: " + System.getenv("PROD_DB_PORT"));

		SpringApplication.run(BeautyProjectApplication.class, args);
	}


}