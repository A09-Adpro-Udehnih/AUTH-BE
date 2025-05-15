package com.example.authbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthBeApplication {

	public static void main(String[] args) {
        String[] activeProfiles = System.getProperty("spring.profiles.active", "").split(",");
        boolean isTestProfile = false;
        for (String profile : activeProfiles) {
            if (profile.trim().equalsIgnoreCase("test")) {
                isTestProfile = true;
                break;
            }
        }
        if (!isTestProfile && !isRunningUnderTest()) {
            System.out.println("Running DB migrations...");
            MigrationManager.migrate();
            System.out.println("Done!");
        }
		SpringApplication.run(AuthBeApplication.class, args);
	}

    private static boolean isRunningUnderTest() {
        String prop = System.getProperty("sun.java.command", "");
        return prop.contains("org.junit") || prop.contains("-test") || prop.contains("Test");
    }
}
