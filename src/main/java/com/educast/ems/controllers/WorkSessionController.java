package com.educast.ems.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.educast.ems.dto.WorkSessionRequestDTO;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/work-sessions")
@RequiredArgsConstructor
public class WorkSessionController {

    // private final WorkSessionService workSessionService; // ✅ Commented for now

    @PostMapping("/clock-in")
    public ResponseEntity<?> clockIn(@RequestBody WorkSessionRequestDTO requestDTO) {
        // WorkSessionResponseDTO responseDTO = workSessionService.clockIn(requestDTO);
        // return ResponseEntity.ok(responseDTO);
        return ResponseEntity.ok("Service not implemented yet"); // Temporary
    }

    @PutMapping("/clock-out/{id}")
    public ResponseEntity<?> clockOut(@PathVariable Long id) {
        // WorkSessionResponseDTO responseDTO = workSessionService.clockOut(id);
        // return ResponseEntity.ok(responseDTO);
        return ResponseEntity.ok("Service not implemented yet"); // Temporary
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getByEmployee(@PathVariable Long employeeId) {
        // List<WorkSessionResponseDTO> sessions = workSessionService.getSessionsByEmployee(employeeId);
        // return ResponseEntity.ok(sessions);
        return ResponseEntity.ok("Service not implemented yet"); // Temporary
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        // WorkSessionResponseDTO responseDTO = workSessionService.getSessionById(id);
        // return ResponseEntity.ok(responseDTO);
        return ResponseEntity.ok("Service not implemented yet"); // Temporary
    }
}
