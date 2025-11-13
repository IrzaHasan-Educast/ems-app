package com.educast.ems.services;

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
    public User updateUser(Long userId, String newUsername, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        // Update username if provided
        if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(user.getUsername())) {
            userRepository.findByUsername(newUsername).ifPresent(u -> {
                throw new IllegalArgumentException("Username already exists");
            });
            user.setUsername(newUsername);
        }

        // Update password if provided
        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters");
            }
            user.setPasswordHash(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user);
    }
}
