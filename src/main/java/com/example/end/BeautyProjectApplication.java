package com.example.end;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import jakarta.annotation.PreDestroy;

@SpringBootApplication
@EnableAsync
public class BeautyProjectApplication {

//	private SSHConnectionManager sshManager;

	public static void main(String[] args) {
		SpringApplication.run(BeautyProjectApplication.class, args);
	}

//	@Bean
//	public SSHConnectionManager sshConnectionManager() {
//		sshManager = new SSHConnectionManager();
//		try {
//			sshManager.connect();
//			System.out.println("SSH tunnel successfully established.");
//		} catch (Exception e) {
//			System.err.println("Error establishing SSH tunnel!");
//			e.printStackTrace();
//			System.exit(1); // Exit if SSH connection fails
//		}
//		return sshManager;
//	}
//
//	@PreDestroy
//	public void cleanup() {
//		if (sshManager != null) {
//			sshManager.disconnect();
//			System.out.println("SSH tunnel closed.");
//		}
//	}
}