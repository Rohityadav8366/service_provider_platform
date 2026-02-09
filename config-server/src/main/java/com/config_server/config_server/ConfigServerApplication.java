package com.config_server.config_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

	public static void main(String[] args) {

		SpringApplication.run(ConfigServerApplication.class, args);
		System.out.println("========================================");
		System.out.println("Config Server Started Successfully!");
		System.out.println("Server URL: http://localhost:8888");
		System.out.println("========================================");
	}

}
