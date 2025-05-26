package com.example.authbe;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AuthBeApplicationTests {

	@Test
	void contextLoads() {
		assertDoesNotThrow(() -> {
            String[] args = {};
            AuthBeApplication.main(args);
        });
	}

}
