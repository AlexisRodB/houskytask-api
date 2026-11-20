package com.houskytask.houskytask_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private LocalTime time;
    private String project;
    private TaskStatus status;
    private TaskPriority priority;

    public void validate() {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la tarea es obligatorio.");
        }
        if (status == null) {
            this.status = TaskStatus.PENDIENTE;
        }
        if (priority == null) {
            this.priority = TaskPriority.MEDIA;
        }
        if (project == null || project.trim().isEmpty()) {
            this.project = "General";
        }
    }
}