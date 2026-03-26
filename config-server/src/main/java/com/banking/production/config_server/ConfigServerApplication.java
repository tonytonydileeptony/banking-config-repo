package com.banking.production.config_server;

// Spring Boot application starter
import org.springframework.boot.SpringApplication;
// Spring Boot auto-configuration annotation
import org.springframework.boot.autoconfigure.SpringBootApplication;
// Spring Cloud Config Server annotation
import org.springframework.cloud.config.server.EnableConfigServer;

// Enables this application as a Spring Cloud Config Server
// This server will serve configuration to all microservices
@EnableConfigServer
// Marks this class as a Spring Boot application with auto-configuration
@SpringBootApplication
public class ConfigServerApplication {

	// Main method - entry point for the application
	public static void main(String[] args) {
		// Start the Spring Boot application context
		SpringApplication.run(ConfigServerApplication.class, args);
	}

}
