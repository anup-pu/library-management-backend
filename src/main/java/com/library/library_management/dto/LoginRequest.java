package com.library.library_management.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String identifier;
    private String password;
}
