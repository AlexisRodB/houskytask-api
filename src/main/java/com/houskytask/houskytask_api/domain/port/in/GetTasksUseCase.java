package com.houskytask.houskytask_api.domain.port.in;

import com.houskytask.houskytask_api.domain.model.Task;
import java.util.List;

public interface GetTasksUseCase {
    List<Task> getAllTasks();
    Task getTaskById(Long id);
}