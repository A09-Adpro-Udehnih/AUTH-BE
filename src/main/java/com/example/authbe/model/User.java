package com.example.authbe.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.authbe.enums.Role;

@Data
@Entity
@Table(name = "users")
@Builder
public class User {
   
    public User() {
    }

    public User(UUID id, String email, String fullName, String password, Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
    }
}
