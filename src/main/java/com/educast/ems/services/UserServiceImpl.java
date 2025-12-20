package com.educast.ems.services;

import com.educast.ems.dto.UserResponse;
import com.educast.ems.models.User;
import com.educast.ems.repositories.UserRepository;
import lombok.RequiredArgsConstructor;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse updateUser(Long id, String newUsername, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));

        if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(user.getUsername())) {
            userRepository.findByUsername(newUsername).ifPresent(u -> {
                throw new IllegalArgumentException("Username already exists");
            });
            user.setUsername(newUsername);
        }

        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters");
            }
            user.setPasswordHash(passwordEncoder.encode(newPassword));
        }

        User saved = userRepository.save(user);
        return mapToResponse(saved);
        
    }
    
    @Override
    public UserResponse getUserByEmployeeId(Long employeeId) {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("User not found for employee id " + employeeId));
        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setUsername(user.getUsername());
        res.setRole(user.getRole().name());
        return res;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }


}
