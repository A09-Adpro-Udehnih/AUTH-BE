package com.example.authbe.model;

import org.junit.jupiter.api.Test;

import com.example.authbe.enums.Role;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

    @Test
    void testUserBuilderAndGetters() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        String fullName = "Test User";
        String password = "password123";
        Role role = Role.STUDENT;
        LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        User user = User.builder()
                .id(userId)
                .email(email)
                .fullName(fullName)
                .password(password)
                .role(role)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertEquals(userId, user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(fullName, user.getFullName());
        assertEquals(password, user.getPassword());
        assertEquals(role, user.getRole());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    @Test
    void testUserSetters() {
        User user = new User();
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        String fullName = "Test User";
        String password = "password123";
        Role role = Role.STUDENT;
        LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        user.setId(userId);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPassword(password);
        user.setRole(role);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);

        // Then
        assertEquals(userId, user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(fullName, user.getFullName());
        assertEquals(password, user.getPassword());
        assertEquals(role, user.getRole());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    @Test
    void testEquals() {
        UUID userId = UUID.randomUUID();
        
        User user1 = User.builder()
                .id(userId)
                .email("test@example.com")
                .fullName("Test User")
                .password("password123")
                .role(Role.STUDENT)
                .build();
        
        User user2 = User.builder()
                .id(userId)
                .email("test@example.com")
                .fullName("Test User")
                .password("password123")
                .role(Role.STUDENT)
                .build();
        
        User user3 = User.builder()
                .id(UUID.randomUUID())
                .email("different@example.com")
                .fullName("Different User")
                .password("different123")
                .role(Role.TUTOR)
                .build();

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertNotEquals(user2, user3);
    }

    @Test
    void testHashCode() {
        UUID userId = UUID.randomUUID();
        
        User user1 = User.builder()
                .id(userId)
                .email("test@example.com")
                .fullName("Test User")
                .password("password123")
                .role(Role.STUDENT)
                .build();
        
        User user2 = User.builder()
                .id(userId)
                .email("test@example.com")
                .fullName("Test User")
                .password("password123")
                .role(Role.STUDENT)
                .build();

        // Then
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .fullName("Test User")
                .password("password123")
                .role(Role.STUDENT)
                .build();

        String userString = user.toString();

        assertTrue(userString.contains("id=" + userId));
        assertTrue(userString.contains("email=test@example.com"));
        assertTrue(userString.contains("fullName=Test User"));
        assertTrue(userString.contains("password=password123"));
        assertTrue(userString.contains("role=STUDENT"));
    }

    @Test
    void testPrePersist() {
        User user = new User();
        
        user.onCreate();

        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        
        LocalDateTime now = LocalDateTime.now();
        assertTrue(ChronoUnit.SECONDS.between(user.getCreatedAt(), now) < 2);
        assertTrue(ChronoUnit.SECONDS.between(user.getUpdatedAt(), now) < 2);
    }

    @Test
    void testPreUpdate() {
        User user = new User();
        user.setCreatedAt(LocalDateTime.now().minusDays(1)); 
        LocalDateTime oldUpdatedAt = LocalDateTime.now().minusHours(1);
        user.setUpdatedAt(oldUpdatedAt);
        
        user.onUpdate();
        
        assertNotEquals(oldUpdatedAt, user.getUpdatedAt());
        
        LocalDateTime now = LocalDateTime.now();
        assertTrue(ChronoUnit.SECONDS.between(user.getUpdatedAt(), now) < 2);
    }

    @Test
    void testNoArgsConstructor() {
        User user = new User();
        
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getFullName());
        assertNull(user.getPassword());
        assertNull(user.getRole());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }
    
    @Test
    void testAllArgsConstructor() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        String fullName = "Test User";
        String password = "password123";
        Role role = Role.STUDENT;
        LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        
        User user = new User(userId, email, fullName, password, role, createdAt, updatedAt);
        
        assertEquals(userId, user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(fullName, user.getFullName());
        assertEquals(password, user.getPassword());
        assertEquals(role, user.getRole());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
    }
}