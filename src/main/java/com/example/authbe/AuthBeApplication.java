package com.example.authbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthBeApplication {

	public static void main(String[] args) {
		System.out.println("Running DB migrations...");
        MigrationManager.migrate();
        System.out.println("Done!");
		SpringApplication.run(AuthBeApplication.class, args);
	}

}
