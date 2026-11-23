package com.houskytask.houskytask_api.domain.port.out;

import com.houskytask.houskytask_api.domain.model.OtpVerification;

import java.util.Optional;

public interface OtpRepositoryPort {

    void save(OtpVerification otp);

    Optional<OtpVerification> findByEmail(String email);

    void deleteByEmail(String email);
}
