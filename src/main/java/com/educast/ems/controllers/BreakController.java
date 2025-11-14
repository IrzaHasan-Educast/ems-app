package com.educast.ems.controllers;

import com.educast.ems.dto.BreakRequestDTO;
import com.educast.ems.models.Break;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1/breaks")
@RequiredArgsConstructor
public class BreakController {

    // private final BreakService breakService; // ✅ Commented for now

    @PostMapping
    public ResponseEntity<?> addBreak(@RequestBody BreakRequestDTO requestDTO) {
        // BreakResponseDTO responseDTO = breakService.addBreak(requestDTO);
        // return ResponseEntity.ok(responseDTO);
        return ResponseEntity.ok("Service not implemented yet"); // Temporary
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getBreaksBySession(@PathVariable Long sessionId) {
        // List<BreakResponseDTO> breaks = breakService.getBreaksBySession(sessionId);
        // return ResponseEntity.ok(breaks);
        return ResponseEntity.ok("Service not implemented yet"); // Temporary
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBreak(@PathVariable Long id, @RequestBody BreakRequestDTO requestDTO) {
        // BreakResponseDTO responseDTO = breakService.updateBreak(id, requestDTO);
        // return ResponseEntity.ok(responseDTO);
        return ResponseEntity.ok("Service not implemented yet"); // Temporary
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBreak(@PathVariable Long id) {
        // breakService.deleteBreak(id);
        // return ResponseEntity.noContent().build();
        return ResponseEntity.ok("Service not implemented yet"); // Temporary
    }
}
