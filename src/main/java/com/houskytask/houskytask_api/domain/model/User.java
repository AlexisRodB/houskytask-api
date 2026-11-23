package com.houskytask.houskytask_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String role;
    private String bio;
    private String avatarUrl;
    private String phone;
    private String contactEmail;

    @Builder.Default
    private Map<String, String> socials = new HashMap<>();

    public void validate() {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Ingresa un correo electrónico válido.");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");
        }
    }
}