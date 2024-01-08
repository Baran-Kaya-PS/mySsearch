package com.example.mysearch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void accessUnsecuredResourceShouldSucceed() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    public void accessSecuredResourceUnauthenticatedShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void accessSecuredResourceAsUserShouldFail() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void accessSecuredResourceAsAdminShouldSucceed() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk());
    }

    @Test
    public void loginWithValidCsrfTokenShouldSucceed() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andReturn();

        // Extract the CsrfToken object from the request attribute
        CsrfToken csrfToken = (CsrfToken) mvcResult.getRequest().getAttribute(CsrfToken.class.getName());

        // Now we can use the token value in the subsequent POST request
        mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .param(csrfToken.getParameterName(), csrfToken.getToken()))
                .andExpect(status().is3xxRedirection());
    }



    @Test
    public void loginWithInvalidCsrfTokenShouldFail() throws Exception {
        mockMvc.perform(post("/login").with(SecurityMockMvcRequestPostProcessors.csrf().useInvalidToken())
                        .param("username", "user")
                        .param("password", "password"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void loginWithValidUserShouldSucceed() throws Exception {
        mockMvc.perform(post("/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // This will add the CSRF token
                        .param("username", "user")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accueil")); // assuming you redirect to "/accueil" on success
    }

}
