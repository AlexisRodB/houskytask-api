package com.houskytask.houskytask_api.infrastructure.adapter.in.web.mapper;

import com.houskytask.houskytask_api.domain.model.Task;
import com.houskytask.houskytask_api.domain.model.TaskPriority;
import com.houskytask.houskytask_api.domain.model.TaskStatus;
import com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto.TaskRequestDTO;
import com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto.TaskResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TaskWebMapper {

    public Task toDomain(TaskRequestDTO dto) {
        if (dto == null) return null;
        return Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .dueDate(dto.getDueDate())
                .time(dto.getTime())
                .project(dto.getProject())
                .status(parseStatus(dto.getStatus()))
                .priority(parsePriority(dto.getPriority()))
                .build();
    }

    public TaskResponseDTO toResponse(Task task) {
        if (task == null) return null;
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .time(task.getTime())
                .project(task.getProject())
                .status(formatStatus(task.getStatus()))
                .priority(task.getPriority() != null ? task.getPriority().name().toLowerCase() : "media")
                .build();
    }

    private TaskStatus parseStatus(String status) {
        if (status == null || status.isBlank()) return null;
        return TaskStatus.valueOf(status.trim().toUpperCase().replace("-", "_"));
    }

    private String formatStatus(TaskStatus status) {
        if (status == null) return "pendiente";
        return status.name().toLowerCase().replace("_", "-");
    }

    private TaskPriority parsePriority(String priority) {
        if (priority == null || priority.isBlank()) return null;
        return TaskPriority.valueOf(priority.trim().toUpperCase());
    }
}