package com.library.library_management.dto;

import com.library.library_management.model.Role;
import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String email;
    private String password;
    private Role role;  // ADMIN or STUDENT
}
