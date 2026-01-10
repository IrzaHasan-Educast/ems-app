package com.educast.ems.dto;

import com.educast.ems.models.Role;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String password;
    private Role role;
}
