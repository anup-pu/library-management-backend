package com.library.library_management.dto;

import com.library.library_management.model.Role;
import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String username;
    private String email;
    private Role role;
}
