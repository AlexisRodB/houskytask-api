package com.houskytask.houskytask_api.infrastructure.adapter.in.web.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}
