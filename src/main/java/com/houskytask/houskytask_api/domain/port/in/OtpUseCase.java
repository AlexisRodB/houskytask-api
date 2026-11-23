package com.houskytask.houskytask_api.domain.port.in;

public interface OtpUseCase {

    void sendOtp(String email);

    void verifyOtp(String email, String otpCode);
}
