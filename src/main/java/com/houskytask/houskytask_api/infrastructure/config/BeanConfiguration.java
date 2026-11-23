package com.houskytask.houskytask_api.infrastructure.config;

import com.houskytask.houskytask_api.application.service.AuthService;
import com.houskytask.houskytask_api.application.service.OtpService;
import com.houskytask.houskytask_api.domain.port.in.AuthUseCase;
import com.houskytask.houskytask_api.domain.port.in.GetUsersUseCase;
import com.houskytask.houskytask_api.domain.port.in.OtpUseCase;
import com.houskytask.houskytask_api.domain.port.in.UpdateUserProfileUseCase;
import com.houskytask.houskytask_api.domain.port.out.EmailSenderPort;
import com.houskytask.houskytask_api.domain.port.out.OtpRepositoryPort;
import com.houskytask.houskytask_api.domain.port.out.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;

@Configuration
public class BeanConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public OtpService otpService(OtpRepositoryPort otpRepositoryPort,
                                 UserRepositoryPort userRepositoryPort,
                                 EmailSenderPort emailSenderPort,
                                 PasswordEncoder passwordEncoder,
                                 Clock clock) {
        return new OtpService(otpRepositoryPort, userRepositoryPort, emailSenderPort, passwordEncoder, clock);
    }

    @Bean
    public OtpUseCase otpUseCase(OtpService otpService) {
        return otpService;
    }

    @Bean
    public AuthService authService(UserRepositoryPort userRepositoryPort,
                                   PasswordEncoder passwordEncoder,
                                   OtpUseCase otpUseCase) {
        return new AuthService(userRepositoryPort, passwordEncoder, otpUseCase);
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
