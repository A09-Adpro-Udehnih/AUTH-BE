package com.example.authbe.controller;

import com.example.authbe.dto.auth.AuthResponse;
import com.example.authbe.service.JwtService;
import com.example.authbe.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "test@example.com")
    void getProfile_Success() throws Exception {
        AuthResponse response = AuthResponse.builder()
                .email("test@example.com")
                .fullName("Test User")
                .role("STUDENT")
                .build();

        when(userService.getProfile(any(UUID.class))).thenReturn(response);

        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void updateProfile_Success() throws Exception {
        AuthResponse response = AuthResponse.builder()
                .email("test@example.com")
                .fullName("New Name")
                .role("STUDENT")
                .build();

        when(userService.updateProfile(any(UUID.class), any(), any(), any())).thenReturn(response);

        mockMvc.perform(put("/users/profile")
                .param("fullName", "New Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.fullName").value("New Name"))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }
} 