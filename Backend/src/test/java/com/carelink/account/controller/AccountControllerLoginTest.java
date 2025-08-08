package com.carelink.account.controller;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:carelink_test;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.flyway.enabled=false",
    "sendgrid.enabled=false",
    "notifications.enabled=false",
    "storage.enabled=false",
    "scheduling.enabled=false"
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AccountControllerLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String username;
    private static String generatedPassword;
    private static String uniqueEmail;

    @Test
    @Order(1)
    @DisplayName("Register user successfully")
    void registerUser_success() throws Exception {
        uniqueEmail = "test." + System.currentTimeMillis() + "@example.com";
        String registerJson = "{" +
                "\"firstName\":\"Clara\"," +
                "\"lastName\":\"Dramatica\"," +
                "\"email\":\"" + uniqueEmail + "\"}";

        String registerResponse = mockMvc.perform(
                        post("/api/account/register").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.generatedPassword").exists())
                .andExpect(jsonPath("$.email").value(uniqueEmail))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode registerNode = objectMapper.readTree(registerResponse);
        username = registerNode.path("username").asText();
        generatedPassword = registerNode.path("generatedPassword").asText();

        assertThat(username).isNotBlank();
        assertThat(generatedPassword).isNotBlank();
    }

    @Test
    @Order(2)
    @DisplayName("Login user successfully with generated credentials")
    void loginUser_success() throws Exception {
        assertThat(username).isNotBlank();
        assertThat(generatedPassword).isNotBlank();

        String loginJson = "{" +
                "\"username\":\"" + username + "\"," +
                "\"password\":\"" + generatedPassword + "\"}";

        mockMvc.perform(
                        post("/api/account/login").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(uniqueEmail));
    }
}
