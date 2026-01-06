package com.educast.ems.controllers;

import com.educast.ems.dto.UserResponse;
import com.educast.ems.dto.UserUpdateRequest;
import com.educast.ems.security.CustomUserDetails;
import com.educast.ems.services.UserService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN','HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("employeeId", userDetails.getEmployeeId());
        resp.put("fullName", userDetails.getFullName());
        resp.put("role", userDetails.getRole());
        resp.put("designation", userDetails.getDesignation());
        return ResponseEntity.ok(resp);
    }

}

