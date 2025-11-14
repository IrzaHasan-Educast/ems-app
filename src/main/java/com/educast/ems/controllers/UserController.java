package com.educast.ems.controllers;

import com.educast.ems.dto.UpdateUserRequest;
import com.educast.ems.dto.UserResponse;
import com.educast.ems.dto.UserUpdateRequest;
import com.educast.ems.models.User;
import com.educast.ems.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Fetch user by employee id
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR') or principal == #employeeId")
    public UserResponse getUserByEmployeeId(@PathVariable Long employeeId) {
        return userService.getUserByEmployeeId(employeeId);
    }

    // Update user
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request.getUsername(), request.getPassword());
    }
}

