package com.houskytask.houskytask_api.domain.port.in;

import com.houskytask.houskytask_api.domain.model.Task;

public interface UpdateTaskUseCase {
    Task updateTask(Long id, Task task);
}