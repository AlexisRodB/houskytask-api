package com.houskytask.houskytask_api.infrastructure.config;

import com.houskytask.houskytask_api.application.service.AuthService;
import com.houskytask.houskytask_api.domain.port.in.AuthUseCase;
import com.houskytask.houskytask_api.domain.port.in.GetUsersUseCase;
import com.houskytask.houskytask_api.domain.port.in.UpdateUserProfileUseCase;
import com.houskytask.houskytask_api.domain.port.out.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfiguration {

    @Bean
    public AuthService authService(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        return new AuthService(userRepositoryPort, passwordEncoder);
    }

    @Bean
    public AuthUseCase authUseCase(AuthService authService) {
        return authService;
    }

    @Bean
    public GetUsersUseCase getUsersUseCase(AuthService authService) {
        return authService;
    }

    @Bean
    public UpdateUserProfileUseCase updateUserProfileUseCase(AuthService authService) {
        return authService;
    }
}
