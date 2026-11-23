package com.houskytask.houskytask_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerification {

    private String email;
    private String codeHash;
    private LocalDateTime expiresAt;
    private int attempts;

    public boolean isExpired(LocalDateTime now) {
        return !expiresAt.isAfter(now);
    }

    public void registerFailedAttempt() {
        this.attempts++;
    }
}
