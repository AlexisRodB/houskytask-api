package com.houskytask.houskytask_api.application.service;

import com.houskytask.houskytask_api.domain.model.User;
import com.houskytask.houskytask_api.domain.port.in.AuthUseCase;
import com.houskytask.houskytask_api.domain.port.in.GetUsersUseCase;
import com.houskytask.houskytask_api.domain.port.in.UpdateUserProfileUseCase;
import com.houskytask.houskytask_api.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class AuthService implements AuthUseCase, GetUsersUseCase, UpdateUserProfileUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepositoryPort.findAll();
    }

    @Override
    public User register(User user) {
        user.validate();
        user.setEmail(user.getEmail().trim().toLowerCase());

        if (userRepositoryPort.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Este correo ya está registrado.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepositoryPort.save(user);
    }

    @Override
    public User login(String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Debes ingresar correo y contraseña.");
        }

        User user = userRepositoryPort.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales incorrectas."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Credenciales incorrectas.");
        }

        return user;
    }

    @Override
    public User updateProfile(Long id, User updatedData) {
        User existing = userRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        if (updatedData.getFullName() != null && !updatedData.getFullName().isBlank()) {
            existing.setFullName(updatedData.getFullName().trim());
        }
        if (updatedData.getEmail() != null && !updatedData.getEmail().isBlank()) {
            String normalizedEmail = updatedData.getEmail().trim().toLowerCase();
            if (!normalizedEmail.equals(existing.getEmail()) && userRepositoryPort.existsByEmail(normalizedEmail)) {
                throw new IllegalArgumentException("Ese correo ya está en uso por otra cuenta.");
            }
            existing.setEmail(normalizedEmail);
        }

        existing.setBio(updatedData.getBio());
        existing.setAvatarUrl(updatedData.getAvatarUrl());
        existing.setPhone(updatedData.getPhone());
        existing.setContactEmail(updatedData.getContactEmail());
        existing.setSocials(updatedData.getSocials());

        return userRepositoryPort.save(existing);
    }
}
