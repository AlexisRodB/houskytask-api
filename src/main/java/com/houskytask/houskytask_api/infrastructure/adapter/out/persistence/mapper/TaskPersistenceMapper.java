package com.houskytask.houskytask_api.infrastructure.adapter.out.persistence.mapper;

import com.houskytask.houskytask_api.domain.model.Task;
import com.houskytask.houskytask_api.infrastructure.adapter.out.persistence.entity.TaskJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskPersistenceMapper {

    public TaskJpaEntity toEntity(Task task) {
        if (task == null) return null;
        return TaskJpaEntity.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .time(task.getTime())
                .project(task.getProject())
                .status(task.getStatus())
                .priority(task.getPriority())
                .build();
    }

    public Task toDomain(TaskJpaEntity entity) {
        if (entity == null) return null;
        return Task.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .dueDate(entity.getDueDate())
                .time(entity.getTime())
                .project(entity.getProject())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .build();
    }
}