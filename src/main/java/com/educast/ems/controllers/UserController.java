package com.educast.ems.controllers;

import com.educast.ems.dto.UpdateUserRequest;
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

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')") // Only admin/HR can update
    public User updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request.getUsername(), request.getPassword());
    }
}
