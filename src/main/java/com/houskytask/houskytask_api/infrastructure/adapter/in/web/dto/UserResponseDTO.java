package com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String bio;
    private String avatarUrl;
    private String phone;
    private String contactEmail;
    private Map<String, String> socials;
    private String token;
}
