package com.houskytask.houskytask_api.infrastructure.config;

import com.houskytask.houskytask_api.application.service.TaskService;
import com.houskytask.houskytask_api.domain.port.out.TaskRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public TaskService taskService(TaskRepositoryPort taskRepositoryPort) {
        return new TaskService(taskRepositoryPort);
    }
}