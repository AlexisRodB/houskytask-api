package com.houskytask.houskytask_api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.houskytask.houskytask_api.domain.port.in.OtpUseCase;
import com.houskytask.houskytask_api.domain.port.out.EmailSenderPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AuthFlowIntegrationTest.OtpSenderTestConfig.class)
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CapturingEmailSender capturingEmailSender;

    @Autowired
    private OtpUseCase otpUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @TestConfiguration
    static class OtpSenderTestConfig {

        @Bean
        @Primary
        CapturingEmailSender capturingEmailSender() {
            return new CapturingEmailSender();
        }
    }

    static class CapturingEmailSender implements EmailSenderPort {

        private final Map<String, String> codes = new ConcurrentHashMap<>();

        @Override
        public void sendOtpEmail(String toEmail, String code) {
            codes.put(toEmail.trim().toLowerCase(), code);
        }

        String codeFor(String email) {
            return codes.get(email.trim().toLowerCase());
        }
    }

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

    @Test
    void otpFlowRejectsInvalidAttemptsAndConsumesTheCode() throws Exception {
        mockMvc.perform(post("/api/v1/auth/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "invalid-email"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ingresa un correo electrónico válido."));

        String email = "otp-" + System.nanoTime() + "@example.com";
        String code = requestOtp(email);
        String wrongCode = code.equals("000000") ? "111111" : "000000";

        mockMvc.perform(registerRequest("Otp User", email, "secret123", wrongCode))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Código OTP incorrecto."));

        JsonNode registered = registerWithCode("Otp User", email, "secret123", code);
        assertNotNull(registered.get("token"));

        mockMvc.perform(post("/api/v1/auth/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Este correo ya está registrado."));

        IllegalArgumentException consumed = assertThrows(
                IllegalArgumentException.class,
                () -> otpUseCase.verifyOtp(email, code)
        );
        assertEquals("Primero solicita un código de verificación.", consumed.getMessage());

        String bruteEmail = "otp-brute-" + System.nanoTime() + "@example.com";
        String bruteCode = requestOtp(bruteEmail);
        String bruteWrongCode = bruteCode.equals("000000") ? "111111" : "000000";

        for (int attempt = 0; attempt < 5; attempt++) {
            mockMvc.perform(registerRequest("Brute User", bruteEmail, "secret123", bruteWrongCode))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Código OTP incorrecto."));
        }

        mockMvc.perform(registerRequest("Brute User", bruteEmail, "secret123", bruteWrongCode))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Demasiados intentos fallidos. Solicita un código nuevo."));
    }

    private JsonNode registerUser(String fullName, String email, String password) throws Exception {
        String otpCode = requestOtp(email);
        return registerWithCode(fullName, email, password, otpCode);
    }

    private JsonNode registerWithCode(String fullName, String email, String password, String otpCode) throws Exception {
        MvcResult result = mockMvc.perform(registerRequest(fullName, email, password, otpCode))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private MockHttpServletRequestBuilder registerRequest(String fullName, String email, String password, String otpCode) throws Exception {
        return post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "fullName", fullName,
                        "email", email,
                        "password", password,
                        "otpCode", otpCode
                )));
    }

    private String requestOtp(String email) throws Exception {
        mockMvc.perform(post("/api/v1/auth/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Código enviado a tu correo."));

        String code = capturingEmailSender.codeFor(email);
        assertNotNull(code);
        return code;
    }
}
