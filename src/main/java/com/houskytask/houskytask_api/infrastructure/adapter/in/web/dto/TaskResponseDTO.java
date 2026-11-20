package com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private LocalTime time;
    private String project;
    private String status;
    private String priority;
}