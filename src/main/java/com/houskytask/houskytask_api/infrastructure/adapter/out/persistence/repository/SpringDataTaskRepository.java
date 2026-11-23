package com.houskytask.houskytask_api.infrastructure.adapter.out.persistence.repository;

import com.houskytask.houskytask_api.infrastructure.adapter.out.persistence.entity.TaskJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataTaskRepository extends JpaRepository<TaskJpaEntity, Long> {
    List<TaskJpaEntity> findByUserId(Long userId);
}