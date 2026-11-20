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
import org.springframework.web.bind.annotation.*;

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
        Task created = createTaskUseCase.createTask(mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAll() {
        List<TaskResponseDTO> tasks = getTasksUseCase.getAllTasks()
                .stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getById(@PathVariable Long id) {
        Task task = getTasksUseCase.getTaskById(id);
        return ResponseEntity.ok(mapper.toResponse(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> update(@PathVariable Long id, @RequestBody TaskRequestDTO request) {
        Task updated = updateTaskUseCase.updateTask(id, mapper.toDomain(request));
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteTaskUseCase.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}