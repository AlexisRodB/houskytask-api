package com.houskytask.houskytask_api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void registerLoginAndUpdateProfile() throws Exception {
        String email = "test-" + System.nanoTime() + "@example.com";
        String password = "secret123";

        JsonNode registered = registerUser("Test User", email, password);
        long userId = registered.get("id").asLong();
        String token = registered.get("token").asText();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", password
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("bio", "Hola"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bio").value("Hola"));

        mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "userId", userId,
                                "title", "Prueba",
                                "status", "pending",
                                "priority", "medium"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("pendiente"));
    }

    @Test
    void taskOwnershipIsEnforcedFromJwt() throws Exception {
        String stamp = String.valueOf(System.nanoTime());
        JsonNode ownerA = registerUser("Owner A", "ownerA-" + stamp + "@example.com", "secret123");
        JsonNode ownerB = registerUser("Owner B", "ownerB-" + stamp + "@example.com", "secret123");

        long ownerAId = ownerA.get("id").asLong();
        long ownerBId = ownerB.get("id").asLong();
        String tokenA = ownerA.get("token").asText();
        String tokenB = ownerB.get("token").asText();

        MvcResult creation = mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "userId", ownerBId,
                                "title", "Forged",
                                "status", "pending",
                                "priority", "medium"
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createdTask = objectMapper.readTree(creation.getResponse().getContentAsString());
        long taskId = createdTask.get("id").asLong();
        assertEquals(ownerAId, createdTask.get("userId").asLong());

        mockMvc.perform(get("/api/v1/tasks")
                        .param("userId", String.valueOf(ownerBId))
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/tasks")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/v1/tasks/{id}", taskId)
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "Hacked"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/v1/tasks/{id}", taskId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/v1/tasks/{id}", taskId)
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "Owner update"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(ownerAId));

        mockMvc.perform(delete("/api/v1/tasks/{id}", taskId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isNoContent());
    }

    private JsonNode registerUser(String fullName, String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "fullName", fullName,
                                "email", email,
                                "password", password
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
