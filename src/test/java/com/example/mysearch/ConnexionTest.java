package com.example.mysearch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
class ConnexionTest {

    @Autowired
    private MockMvc mockMvc;

    // This method will be called before each test execution
    @BeforeEach
    void setUp() {
        // You can add any common setup code here, if needed
    }

    @Test
    void validUserShouldLoginSuccessfully() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "validUser")
                        .param("password", "validPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/")); // Replace with the expected redirect URL after successful login
    }

    @Test
    void invalidUserShouldNotLogin() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "invalidUser")
                        .param("password", "invalidPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true")); // Replace with the expected redirect URL after failed login
    }

    @Test
    void validUserWithWrongPasswordShouldNotLogin() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "validUser")
                        .param("password", "wrongPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void shouldRedirectAfterSuccessfulLogin() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "validUser")
                        .param("password", "validPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/")); // Adjust the redirect URL as per your application's flow
    }

    @Test
    void shouldRedirectAfterFailedLogin() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "validUser")
                        .param("password", "wrongPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    @WithMockUser
    void userShouldLogoutSuccessfully() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout")); // Adjust the redirect URL to the expected URL after logout
    }
}