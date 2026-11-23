package com.houskytask.houskytask_api.domain.port.in;

import com.houskytask.houskytask_api.domain.model.User;
import java.util.List;

public interface GetUsersUseCase {
    List<User> getAllUsers();
}