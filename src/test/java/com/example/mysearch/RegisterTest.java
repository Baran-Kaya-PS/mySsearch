package com.example.mysearch;

import com.example.mysearch.service.SignupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;
@SpringBootTest
@AutoConfigureMockMvc
class RegistrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignupService signupService;

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
    public void registrationWithExistingUsername() throws Exception {
        // Set up the test data
        String existingUsername = "testuser";
        String newEmail = "newuser@example.com";
        String newPassword = "newuserpassword";

        // Mock the behavior of the service
        when(signupService.userExists(existingUsername)).thenReturn(true);

        // Perform the registration request
        mockMvc.perform(post("/inscription")
                        .param("name", existingUsername)
                        .param("email", newEmail)
                        .param("password", newPassword))

                // Check the response status and view name
                .andExpect(status().isBadRequest())
                .andExpect(view().name("signup"))

                // Check for the expected error message
                .andExpect(model().attributeHasFieldErrors("user", "name"))
                .andExpect(model().attributeHasFieldErrorCode("user", "name", "error.user"));
    }
}
