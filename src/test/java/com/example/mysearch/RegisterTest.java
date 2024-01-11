package com.example.mysearch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void successfulRegistration() throws Exception {
        mockMvc.perform(post("/inscription")
                        .param("name", "newUser")
                        .param("password", "newPassword")
                        .param("email", "newuser@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login")); // Assuming the user is redirected to the login page after successful registration
    }

    @Test
    void registrationWithExistingUsername() throws Exception {
        mockMvc.perform(post("/inscription")
                        .param("name", "existingUser")
                        .param("password", "passwordForExistingUser")
                        .param("email", "existinguser@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeHasFieldErrors("username"));
    }
}
