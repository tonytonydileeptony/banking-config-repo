package com.banking.production.auth_server;

// Spring Boot starter class bootstrap
import org.springframework.boot.SpringApplication;
// Indicates this is a Spring Boot application with component scanning and auto-configuration
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Marks this class as a Spring Boot application entry point with auto-configuration enabled
@SpringBootApplication

public class AuthServerApplication {

	// Main method - entry point for the Java application
	public static void main(String[] args) {
		// Start the Spring Boot application context
		SpringApplication.run(AuthServerApplication.class, args);
	}

}
