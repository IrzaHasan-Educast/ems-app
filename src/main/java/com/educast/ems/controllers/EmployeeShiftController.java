package com.educast.ems.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.educast.ems.dto.EmployeeShiftRequestDTO;
import com.educast.ems.dto.EmployeeShiftResponseDTO;
import com.educast.ems.services.EmployeeShiftService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employee-shifts")
@RequiredArgsConstructor
public class EmployeeShiftController {

	@Autowired
    private final EmployeeShiftService employeeShiftService;


    // Assign shift to employee
    @PostMapping("/assign")
    public ResponseEntity<String> assignShift(@RequestBody EmployeeShiftRequestDTO dto) {
        employeeShiftService.assignedShift(dto.getEmployeeId(), dto.getShiftId());
        return ResponseEntity.ok("Shift assigned successfully");
    }

    // Update assigned shift
    @PutMapping("/update")
    public ResponseEntity<String> updateAssignedShift(@RequestBody EmployeeShiftRequestDTO dto) {
        employeeShiftService.updateAssignedShift(dto);
        return ResponseEntity.ok("Assigned shift updated successfully");
    }

    // Delete assigned shift
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAssignedShift(@PathVariable Long id) {
        employeeShiftService.deleteAssignedShift(id);
        return ResponseEntity.ok("Assigned shift deleted successfully");
    }
}

