package com.houskytask.houskytask_api.infrastructure.adapter.in.web.controller;

import com.houskytask.houskytask_api.domain.model.Task;
import com.houskytask.houskytask_api.domain.port.in.CreateTaskUseCase;
import com.houskytask.houskytask_api.domain.port.in.DeleteTaskUseCase;
import com.houskytask.houskytask_api.domain.port.in.GetTasksUseCase;
import com.houskytask.houskytask_api.domain.port.in.UpdateTaskUseCase;
import com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto.TaskRequestDTO;
import com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto.TaskResponseDTO;
import com.houskytask.houskytask_api.infrastructure.adapter.in.web.mapper.TaskWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final GetTasksUseCase getTasksUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final TaskWebMapper mapper;

    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(@RequestBody TaskRequestDTO request) {
        request.setUserId(getAuthenticatedUserId());
        Task created = createTaskUseCase.createTask(mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAll(@RequestParam(required = false) Long userId) {
        Long authenticatedUserId = getAuthenticatedUserId();
        if (userId != null && !userId.equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<TaskResponseDTO> tasks = getTasksUseCase.getAllTasksByUserId(authenticatedUserId)
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getById(@PathVariable Long id) {
        Task task = getTasksUseCase.getTaskById(id);
        if (!belongsToAuthenticatedUser(task)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(mapper.toResponse(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> update(@PathVariable Long id, @RequestBody TaskRequestDTO request) {
        Task existing = getTasksUseCase.getTaskById(id);
        if (!belongsToAuthenticatedUser(existing)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        request.setUserId(getAuthenticatedUserId());
        Task updated = updateTaskUseCase.updateTask(id, mapper.toDomain(request));
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Task existing = getTasksUseCase.getTaskById(id);
        if (!belongsToAuthenticatedUser(existing)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        deleteTaskUseCase.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    private boolean belongsToAuthenticatedUser(Task task) {
        return task.getUserId() != null && task.getUserId().equals(getAuthenticatedUserId());
    }

    private Long getAuthenticatedUserId() {
        return (Long) org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
