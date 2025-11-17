package com.educast.ems.controllers;

import com.educast.ems.dto.BreakRequestDTO;
import com.educast.ems.dto.BreakResponseDTO;
import com.educast.ems.services.BreakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/breaks")
@RequiredArgsConstructor
public class BreakController {

    private final BreakService breakService;

    // Start break
    @PostMapping
    public ResponseEntity<BreakResponseDTO> startBreak(@RequestBody BreakRequestDTO requestDTO) {
        return ResponseEntity.ok(breakService.addBreak(requestDTO));
    }
    
    @GetMapping("/active/{sessionId}")
    public ResponseEntity<BreakResponseDTO> getActiveBreak(@PathVariable Long sessionId) {
        BreakResponseDTO activeBreak = breakService.getActiveBreak(sessionId);
        return ResponseEntity.ok(activeBreak); // returns null if no active break
    }


    // End break
    @PutMapping("/{id}")
    public ResponseEntity<BreakResponseDTO> endBreak(@PathVariable Long id) {
        BreakRequestDTO dto = new BreakRequestDTO();
        return ResponseEntity.ok(breakService.updateBreak(id, dto));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<BreakResponseDTO>> getBreaksBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(breakService.getBreaksBySession(sessionId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBreak(@PathVariable Long id) {
        breakService.deleteBreak(id);
        return ResponseEntity.noContent().build();
    }
}
