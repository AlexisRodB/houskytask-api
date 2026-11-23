package com.houskytask.houskytask_api.infrastructure.adapter.in.web.controller;

import com.houskytask.houskytask_api.domain.model.User;
import com.houskytask.houskytask_api.domain.port.in.AuthUseCase;
import com.houskytask.houskytask_api.domain.port.in.GetUsersUseCase;
import com.houskytask.houskytask_api.domain.port.in.OtpUseCase;
import com.houskytask.houskytask_api.domain.port.in.UpdateUserProfileUseCase;
import com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto.LoginRequestDTO;
import com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto.RegisterRequestDTO;
import com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto.SendOtpRequestDTO;
import com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto.UpdateUserProfileRequestDTO;
import com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto.UserResponseDTO;
import com.houskytask.houskytask_api.infrastructure.adapter.in.web.mapper.UserWebMapper;
import com.houskytask.houskytask_api.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;
    private final OtpUseCase otpUseCase;
    private final GetUsersUseCase getUsersUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final JwtService jwtService;
    private final UserWebMapper userWebMapper;

    @PostMapping("/auth/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody SendOtpRequestDTO request) {
        otpUseCase.sendOtp(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Código enviado a tu correo."));
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        User created = authUseCase.register(userWebMapper.toDomain(request), request.getOtpCode());
        String token = jwtService.generateToken(created.getId(), created.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userWebMapper.toResponse(created, token));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody LoginRequestDTO request) {
        User user = authUseCase.login(request.getEmail(), request.getPassword());
        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return ResponseEntity.ok(userWebMapper.toResponse(user, token));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = getUsersUseCase.getAllUsers()
                .stream()
                .map(userWebMapper::toResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @PathVariable Long id,
            @RequestBody UpdateUserProfileRequestDTO request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !(authentication.getPrincipal() instanceof Long authenticatedUserId)
                || !authenticatedUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User updated = updateUserProfileUseCase.updateProfile(id, userWebMapper.toDomain(request));

        return ResponseEntity.ok(userWebMapper.toResponse(updated));
    }
}
