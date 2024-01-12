package com.example.mysearch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    @Test
    void validUserShouldLoginSuccessfully() throws Exception {
        // Créez d'abord une inscription
        mockMvc.perform(post("/inscription")
                        .param("name", "validUser")
                        .param("password", "validPassword")
                        .param("email", "validuser@example.com"))
                .andExpect(status().is3xxRedirection()) // Vous attendez une redirection après une inscription réussie
                .andExpect(redirectedUrl("/login")); // Assurez-vous que cette URL correspond à celle attendue après l'inscription

        // Ensuite, tentez de vous connecter avec l'utilisateur créé
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "validUser")
                        .param("password", "validPassword"))
                .andExpect(status().is3xxRedirection()) // Vous attendez une redirection après une connexion réussie
                .andExpect(redirectedUrl("/index")); // Assurez-vous que cette URL correspond à celle attendue après la connexion réussie
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