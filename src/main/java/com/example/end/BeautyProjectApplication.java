package com.example.end;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BeautyProjectApplication {
	private static final Logger logger = LoggerFactory.getLogger(BeautyProjectApplication.class);

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.directory(".")
				.filename(".env")
				.ignoreIfMissing()
				.load();

		for (var entry : dotenv.entries()) {
			System.setProperty(entry.getKey(), entry.getValue());
			logger.info("Loading env variable: {} = {}", entry.getKey(), entry.getValue().substring(0, Math.min(entry.getValue().length(), 5)) + "...");
		}

		ConfigurableApplicationContext context = SpringApplication.run(BeautyProjectApplication.class, args);
		Environment env = context.getEnvironment();
		
		logger.info("Active profile: {}", env.getActiveProfiles().length > 0 ? env.getActiveProfiles()[0] : "default");
		logger.info("jwt.access.key is set: {}", env.getProperty("jwt.access.key") != null);
		logger.info("jwt.refresh.key is set: {}", env.getProperty("jwt.refresh.key") != null);
		logger.info("ACCESS_KEY is set: {}", env.getProperty("ACCESS_KEY") != null);
		logger.info("REFRESH_KEY is set: {}", env.getProperty("REFRESH_KEY") != null);
		logger.info("CLOUDINARY_CLOUD_NAME is set: {}", env.getProperty("CLOUDINARY_CLOUD_NAME") != null);
		logger.info("CLOUDINARY_API_KEY is set: {}", env.getProperty("CLOUDINARY_API_KEY") != null);
		logger.info("CLOUDINARY_API_SECRET is set: {}", env.getProperty("CLOUDINARY_API_SECRET") != null);
	}

}