package com.houskytask.houskytask_api.application.service;

import com.houskytask.houskytask_api.domain.exception.TaskNotFoundException;
import com.houskytask.houskytask_api.domain.model.Task;
import com.houskytask.houskytask_api.domain.port.in.CreateTaskUseCase;
import com.houskytask.houskytask_api.domain.port.in.DeleteTaskUseCase;
import com.houskytask.houskytask_api.domain.port.in.GetTasksUseCase;
import com.houskytask.houskytask_api.domain.port.in.UpdateTaskUseCase;
import com.houskytask.houskytask_api.domain.port.out.TaskRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService implements CreateTaskUseCase, GetTasksUseCase, UpdateTaskUseCase, DeleteTaskUseCase {

    private final TaskRepositoryPort taskRepositoryPort;

    public TaskService(TaskRepositoryPort taskRepositoryPort) {
        this.taskRepositoryPort = taskRepositoryPort;
    }

    @Override
    public Task createTask(Task task) {
        task.validate();
        return taskRepositoryPort.save(task);
    }

    @Override
    public List<Task> getAllTasksByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario es obligatorio para consultar tareas.");
        }
        return taskRepositoryPort.findByUserId(userId);
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepositoryPort.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    public Task updateTask(Long id, Task updatedTask) {
        Task existingTask = getTaskById(id);

        if (updatedTask.getTitle() != null && !updatedTask.getTitle().trim().isEmpty()) {
            existingTask.setTitle(updatedTask.getTitle());
        }
        if (updatedTask.getDescription() != null) {
            existingTask.setDescription(updatedTask.getDescription());
        }
        if (updatedTask.getDueDate() != null) {
            existingTask.setDueDate(updatedTask.getDueDate());
        }
        if (updatedTask.getTime() != null) {
            existingTask.setTime(updatedTask.getTime());
        }
        if (updatedTask.getProject() != null) {
            existingTask.setProject(updatedTask.getProject());
        }
        if (updatedTask.getStatus() != null) {
            existingTask.setStatus(updatedTask.getStatus());
        }
        if (updatedTask.getPriority() != null) {
            existingTask.setPriority(updatedTask.getPriority());
        }

        return taskRepositoryPort.save(existingTask);
    }

    @Override
    public void deleteTask(Long id) {
        if (!taskRepositoryPort.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepositoryPort.deleteById(id);
    }
}