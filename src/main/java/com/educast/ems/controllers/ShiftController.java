package com.educast.ems.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.educast.ems.dto.ShiftRequestDTO;
import com.educast.ems.dto.ShiftResponseDTO;
import com.educast.ems.services.ShiftService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    // Add shift
    @PostMapping
    public ResponseEntity<String> addShift(@RequestBody ShiftRequestDTO dto) {
        shiftService.addShift(dto);
        System.out.println(dto.getEndsAt());
        return ResponseEntity.ok("Shift added successfully");
    }

    // Update shift
    @PutMapping
    public ResponseEntity<String> updateShift(@RequestBody ShiftRequestDTO dto) {
        shiftService.updateShift(dto);
        return ResponseEntity.ok("Shift updated successfully");
    }

    // Delete shift
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteShift(@PathVariable Long id) {
        shiftService.deleteShift(id);
        return ResponseEntity.ok("Shift deleted successfully");
    }

    // Get all shifts
    @GetMapping
    public ResponseEntity<List<ShiftResponseDTO>> getAllShifts() {
        return ResponseEntity.ok(shiftService.getAllShifts());
    }
}
