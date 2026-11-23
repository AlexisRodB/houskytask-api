package com.houskytask.houskytask_api.application.service;

import com.houskytask.houskytask_api.domain.model.OtpVerification;
import com.houskytask.houskytask_api.domain.port.in.OtpUseCase;
import com.houskytask.houskytask_api.domain.port.out.EmailSenderPort;
import com.houskytask.houskytask_api.domain.port.out.OtpRepositoryPort;
import com.houskytask.houskytask_api.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;

public class OtpService implements OtpUseCase {

    private static final int OTP_DIGITS = 6;
    private static final int EXPIRATION_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 5;

    private final OtpRepositoryPort otpRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final EmailSenderPort emailSenderPort;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random;
    private final Clock clock;

    public OtpService(OtpRepositoryPort otpRepositoryPort,
                      UserRepositoryPort userRepositoryPort,
                      EmailSenderPort emailSenderPort,
                      PasswordEncoder passwordEncoder,
                      Clock clock) {
        this.otpRepositoryPort = otpRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.emailSenderPort = emailSenderPort;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
        this.random = new SecureRandom();
    }

    @Override
    public void sendOtp(String rawEmail) {
        String email = normalizeEmail(rawEmail);

        if (userRepositoryPort.existsByEmail(email)) {
            throw new IllegalArgumentException("Este correo ya está registrado.");
        }

        String code = generateCode();
        OtpVerification otp = OtpVerification.builder()
                .email(email)
                .codeHash(passwordEncoder.encode(code))
                .expiresAt(LocalDateTime.now(clock).plusMinutes(EXPIRATION_MINUTES))
                .attempts(0)
                .build();

        otpRepositoryPort.save(otp);
        emailSenderPort.sendOtpEmail(email, code);
    }

    @Override
    public void verifyOtp(String rawEmail, String providedCode) {
        String email = normalizeEmail(rawEmail);
        String code = providedCode == null ? "" : providedCode.trim();

        if (!code.matches("\\d{" + OTP_DIGITS + "}")) {
            throw new IllegalArgumentException("El código OTP debe tener 6 dígitos.");
        }

        OtpVerification otp = otpRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Primero solicita un código de verificación."));

        if (otp.isExpired(LocalDateTime.now(clock))) {
            otpRepositoryPort.deleteByEmail(email);
            throw new IllegalArgumentException("El código ha expirado. Solicita uno nuevo.");
        }

        if (otp.getAttempts() >= MAX_ATTEMPTS) {
            otpRepositoryPort.deleteByEmail(email);
            throw new IllegalArgumentException("Demasiados intentos fallidos. Solicita un código nuevo.");
        }

        if (!passwordEncoder.matches(code, otp.getCodeHash())) {
            otp.registerFailedAttempt();
            otpRepositoryPort.save(otp);
            throw new IllegalArgumentException("Código OTP incorrecto.");
        }

        otpRepositoryPort.deleteByEmail(email);
    }

    private String normalizeEmail(String rawEmail) {
        if (rawEmail == null || !rawEmail.contains("@")) {
            throw new IllegalArgumentException("Ingresa un correo electrónico válido.");
        }
        return rawEmail.trim().toLowerCase();
    }

    private String generateCode() {
        return String.format("%0" + OTP_DIGITS + "d", random.nextInt(1_000_000));
    }
}
