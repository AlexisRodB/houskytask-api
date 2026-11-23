package com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto;

import lombok.Data;

import java.util.Map;

@Data
public class UpdateUserProfileRequestDTO {
    private String fullName;
    private String email;
    private String bio;
    private String avatarUrl;
    private String phone;
    private String contactEmail;
    private Map<String, String> socials;
}
