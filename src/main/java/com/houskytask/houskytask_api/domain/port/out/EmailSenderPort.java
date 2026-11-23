package com.houskytask.houskytask_api.domain.port.out;

public interface EmailSenderPort {

    void sendOtpEmail(String toEmail, String code);
}
