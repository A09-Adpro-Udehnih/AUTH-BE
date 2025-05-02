package com.example.authbe.repository;

import com.example.authbe.model.User;
import com.example.authbe.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        // Create a test user
        testUser = User.builder()
                .email(TEST_EMAIL)
                .password("password123")
                .fullName("John Doe")
                .role(Role.STUDENT)
                .build();
        
        // Clear the repository before each test
        userRepository.deleteAll();
    }

    @Test
    void testSaveUser() {
        // Save the user
        User savedUser = userRepository.save(testUser);
        
        // Assert that the user is saved with an ID
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
        assertThat(savedUser.getRole()).isEqualTo(Role.STUDENT);
    }

    @Test
    void testFindByEmail_WhenUserExists() {
        // Save the user using entity manager to ensure it's in the database
        entityManager.persist(testUser);
        entityManager.flush();
        
        // Find the user by email
        Optional<User> foundUser = userRepository.findByEmail(TEST_EMAIL);
        
        // Assert that the user is found
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    void testFindByEmail_WhenUserDoesNotExist() {
        // Try to find a user with an email that doesn't exist
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
        
        // Assert that no user is found
        assertThat(foundUser).isEmpty();
    }

    @Test
    void testExistsByEmail_WhenUserExists() {
        // Save the user using entity manager
        entityManager.persist(testUser);
        entityManager.flush();
        
        // Check if the user exists by email
        boolean exists = userRepository.existsByEmail(TEST_EMAIL);
        
        // Assert that the user exists
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByEmail_WhenUserDoesNotExist() {
        // Check if a user with a non-existent email exists
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        
        // Assert that the user doesn't exist
        assertThat(exists).isFalse();
    }

    @Test
    void testFindById_WhenUserExists() {
        // Save the user using entity manager
        User persistedUser = entityManager.persist(testUser);
        entityManager.flush();
        
        // Find the user by ID
        Optional<User> foundUser = userRepository.findById(persistedUser.getId());
        
        // Assert that the user is found
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    void testFindById_WhenUserDoesNotExist() {
        // Try to find a user with a random ID
        Optional<User> foundUser = userRepository.findById(UUID.randomUUID());
        
        // Assert that no user is found
        assertThat(foundUser).isEmpty();
    }

    @Test
    void testDeleteUser() {
        // Save the user
        User persistedUser = entityManager.persist(testUser);
        entityManager.flush();
        
        // Delete the user
        userRepository.deleteById(persistedUser.getId());
        
        // Try to find the user
        Optional<User> foundUser = userRepository.findById(persistedUser.getId());
        
        // Assert that the user is not found
        assertThat(foundUser).isEmpty();
    }

    @Test
    void testUpdateUser() {
        // Save the user
        User persistedUser = entityManager.persist(testUser);
        entityManager.flush();
        
        // Update the user
        persistedUser.setFullName("Jane Doe");
        persistedUser.setEmail("updated@example.com");
        userRepository.save(persistedUser);
        
        // Find the user
        Optional<User> foundUser = userRepository.findById(persistedUser.getId());
        
        // Assert that the user is updated
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getFullName()).isEqualTo("Jane Doe");
        assertThat(foundUser.get().getEmail()).isEqualTo("updated@example.com");
    }
}