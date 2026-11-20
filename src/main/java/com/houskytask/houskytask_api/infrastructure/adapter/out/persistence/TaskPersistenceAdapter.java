package com.houskytask.houskytask_api.infrastructure.adapter.out.persistence;

import com.houskytask.houskytask_api.domain.model.Task;
import com.houskytask.houskytask_api.domain.port.out.TaskRepositoryPort;
import com.houskytask.houskytask_api.infrastructure.adapter.out.persistence.entity.TaskJpaEntity;
import com.houskytask.houskytask_api.infrastructure.adapter.out.persistence.mapper.TaskPersistenceMapper;
import com.houskytask.houskytask_api.infrastructure.adapter.out.persistence.repository.SpringDataTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskPersistenceAdapter implements TaskRepositoryPort {

    private final SpringDataTaskRepository jpaRepository;
    private final TaskPersistenceMapper mapper;

    @Override
    public Task save(Task task) {
        TaskJpaEntity entity = mapper.toEntity(task);
        TaskJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<Task> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Task> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}