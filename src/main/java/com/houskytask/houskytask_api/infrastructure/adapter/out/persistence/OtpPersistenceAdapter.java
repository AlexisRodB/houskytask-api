package com.houskytask.houskytask_api.infrastructure.adapter.out.persistence;

import com.houskytask.houskytask_api.domain.model.OtpVerification;
import com.houskytask.houskytask_api.domain.port.out.OtpRepositoryPort;
import com.houskytask.houskytask_api.infrastructure.adapter.out.persistence.entity.OtpVerificationJpaEntity;
import com.houskytask.houskytask_api.infrastructure.adapter.out.persistence.repository.OtpJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OtpPersistenceAdapter implements OtpRepositoryPort {

    private final OtpJpaRepository otpRepository;

    @Override
    public void save(OtpVerification otp) {
        otpRepository.save(toEntity(otp));
    }

    @Override
    public Optional<OtpVerification> findByEmail(String email) {
        return otpRepository.findById(email).map(this::toDomain);
    }

    @Override
    public void deleteByEmail(String email) {
        otpRepository.deleteById(email);
    }

    private OtpVerificationJpaEntity toEntity(OtpVerification otp) {
        return OtpVerificationJpaEntity.builder()
                .email(otp.getEmail())
                .codeHash(otp.getCodeHash())
                .expiresAt(otp.getExpiresAt())
                .attempts(otp.getAttempts())
                .build();
    }

    private OtpVerification toDomain(OtpVerificationJpaEntity entity) {
        return OtpVerification.builder()
                .email(entity.getEmail())
                .codeHash(entity.getCodeHash())
                .expiresAt(entity.getExpiresAt())
                .attempts(entity.getAttempts())
                .build();
    }
}
