package com.houskytask.houskytask_api.infrastructure.adapter.out.email;

import com.houskytask.houskytask_api.domain.port.out.EmailSenderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class BrevoEmailAdapter implements EmailSenderPort {

    private final RestClient restClient;
    private final String apiKey;
    private final String senderEmail;

    public BrevoEmailAdapter(@Value("${brevo.api.key:}") String apiKey,
                             @Value("${brevo.sender.email:houskytask@gmail.com}") String senderEmail) {
        this.restClient = RestClient.create();
        this.apiKey = apiKey;
        this.senderEmail = senderEmail;
    }

    @Override
    public void sendOtpEmail(String toEmail, String code) {
        if (apiKey == null || apiKey.isBlank()) {
            log.info("[DEV OTP] Código para {}: {}", toEmail, code);
            return;
        }

        Map<String, Object> body = Map.of(
                "sender", Map.of("name", "HouskyTask", "email", senderEmail),
                "to", List.of(Map.of("email", toEmail)),
                "subject", "Tu código de verificación de HouskyTask: " + code,
                "htmlContent", "<div style='font-family:sans-serif;padding:20px'>" +
                        "<h2>Verifica tu correo en HouskyTask</h2>" +
                        "<p>Usa este código de 6 dígitos para completar tu registro (expira en 10 minutos):</p>" +
                        "<h1 style='letter-spacing:6px;color:#4f46e5'>" + code + "</h1>" +
                        "</div>"
        );

        restClient.post()
                .uri("https://api.brevo.com/v3/smtp/email")
                .header("api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
