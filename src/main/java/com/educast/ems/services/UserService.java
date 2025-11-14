package com.educast.ems.services;

import java.util.Optional;

import com.educast.ems.dto.UserResponse;
import com.educast.ems.models.User;

public interface UserService {
    UserResponse getUserByEmployeeId(Long employeeId);
    UserResponse updateUser(Long userId, String username, String password);
}

