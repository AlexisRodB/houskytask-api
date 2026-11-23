package com.houskytask.houskytask_api.infrastructure.adapter.out.persistence.repository;

import com.houskytask.houskytask_api.infrastructure.adapter.out.persistence.entity.OtpVerificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpJpaRepository extends JpaRepository<OtpVerificationJpaEntity, String> {
}
