package com.houskytask.houskytask_api.infrastructure.adapter.in.web.mapper;

import com.houskytask.houskytask_api.domain.model.Task;
import com.houskytask.houskytask_api.domain.model.TaskPriority;
import com.houskytask.houskytask_api.domain.model.TaskStatus;
import com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto.TaskRequestDTO;
import com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto.TaskResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class TaskWebMapper {

    public Task toDomain(TaskRequestDTO dto) {
        if (dto == null) return null;
        return Task.builder()
                .userId(dto.getUserId())
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
                .userId(task.getUserId())
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
        String value = status.trim().toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');

        return switch (value) {
            case "PENDIENTE", "PENDING" -> TaskStatus.PENDIENTE;
            case "EN_CURSO", "IN_PROGRESS", "INPROGRESS", "PROGRESS" -> TaskStatus.EN_CURSO;
            case "COMPLETADA", "COMPLETED", "DONE" -> TaskStatus.COMPLETADA;
            default -> throw new IllegalArgumentException("Estado de tarea no válido: " + status);
        };
    }

    private String formatStatus(TaskStatus status) {
        if (status == null) return "pendiente";
        return status.name().toLowerCase().replace("_", "-");
    }

    private TaskPriority parsePriority(String priority) {
        if (priority == null || priority.isBlank()) return null;
        String value = priority.trim().toUpperCase(Locale.ROOT);

        return switch (value) {
            case "BAJA", "LOW" -> TaskPriority.BAJA;
            case "MEDIA", "MEDIUM", "MED" -> TaskPriority.MEDIA;
            case "ALTA", "HIGH" -> TaskPriority.ALTA;
            default -> throw new IllegalArgumentException("Prioridad de tarea no válida: " + priority);
        };
    }
}