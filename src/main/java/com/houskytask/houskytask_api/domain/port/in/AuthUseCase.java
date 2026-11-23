package com.houskytask.houskytask_api.domain.port.in;

import com.houskytask.houskytask_api.domain.model.User;

public interface AuthUseCase {
    User register(User user);
    User login(String email, String password);
}