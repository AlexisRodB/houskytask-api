package com.houskytask.houskytask_api.infrastructure.adapter.in.web.mapper;

import com.houskytask.houskytask_api.domain.model.User;
import com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto.RegisterRequestDTO;
import com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto.UpdateUserProfileRequestDTO;
import com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto.UserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserWebMapper {

    public User toDomain(RegisterRequestDTO request) {
        return User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    public User toDomain(UpdateUserProfileRequestDTO request) {
        return User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .bio(request.getBio())
                .avatarUrl(request.getAvatarUrl())
                .phone(request.getPhone())
                .contactEmail(request.getContactEmail())
                .socials(request.getSocials())
                .build();
    }

    public UserResponseDTO toResponse(User user) {
        return toResponse(user, null);
    }

    public UserResponseDTO toResponse(User user, String token) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole() : "USER")
                .bio(user.getBio() != null ? user.getBio() : "")
                .avatarUrl(user.getAvatarUrl() != null ? user.getAvatarUrl() : "")
                .phone(user.getPhone() != null ? user.getPhone() : "")
                .contactEmail(user.getContactEmail() != null ? user.getContactEmail() : "")
                .socials(user.getSocials())
                .token(token)
                .build();
    }
}
