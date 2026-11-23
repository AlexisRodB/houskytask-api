package com.houskytask.houskytask_api.domain.port.out;

import com.houskytask.houskytask_api.domain.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepositoryPort {
    Task save(Task task);
    List<Task> findByUserId(Long userId);
    List<Task> findAll();
    Optional<Task> findById(Long id);
    void deleteById(Long id);
    boolean existsById(Long id);
}