package com.educast.ems.controllers;

import com.educast.ems.dto.AuthRequest;
import com.educast.ems.dto.AuthResponse;
import com.educast.ems.exception.InactiveAccountException;
import com.educast.ems.exception.InvalidPasswordException;
import com.educast.ems.exception.UserNotFoundException;
import com.educast.ems.models.User;
import com.educast.ems.repositories.UserRepository;
import com.educast.ems.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordException("Invalid password");
        }

        if (!user.getEmployee().isActive()) {
            throw new InactiveAccountException("Account is inactive. Contact admin.");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name(),user.getEmployee().getFullName());
    }

}
