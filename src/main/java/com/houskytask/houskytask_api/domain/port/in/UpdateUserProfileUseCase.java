package com.houskytask.houskytask_api.domain.port.in;

import com.houskytask.houskytask_api.domain.model.User;

public interface UpdateUserProfileUseCase {
    User updateProfile(Long id, User updatedData);
}