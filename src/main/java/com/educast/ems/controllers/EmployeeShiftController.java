package com.educast.ems.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.educast.ems.dto.EmployeeShiftRequestDTO;
import com.educast.ems.dto.EmployeeShiftResponseDTO;
import com.educast.ems.services.EmployeeShiftService;

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
		System.out.println("Assign calls");
        System.out.println(dto.getEmployeeId()+ " "+ dto.getShiftId());
	    if (dto.getEmployeeId() == null || dto.getShiftId() == null) {
	        return ResponseEntity.badRequest().body("Employee ID and Shift ID cannot be null");
	    }
	    employeeShiftService.assignedShift(dto.getEmployeeId(), dto.getShiftId());
	    return ResponseEntity.ok("Shift assigned successfully");
	}


    // Update assigned shift
    @PutMapping("/update")
    public ResponseEntity<String> updateAssignedShift(@RequestBody EmployeeShiftRequestDTO dto) {
    	System.out.println("update calls");
        System.out.println(dto.getId()+" "+dto.getEmployeeId()+ " "+ dto.getShiftId());
        employeeShiftService.updateAssignedShift(dto);
        return ResponseEntity.ok("Assigned shift updated successfully");
    }

    // Delete assigned shift
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAssignedShift(@PathVariable Long id) {
        employeeShiftService.deleteAssignedShift(id);
        return ResponseEntity.ok("Assigned shift deleted successfully");
    }
    
    @GetMapping("/employeeId/{empId}")
    public ResponseEntity<EmployeeShiftResponseDTO> getEmployeeShiftByEmployeeId(@PathVariable Long empId) {
        EmployeeShiftResponseDTO dto = employeeShiftService.getEmployeeShiftsByEmpId(empId);
        if (dto == null) {
        	System.out.println(dto == null);
            return ResponseEntity.ok().body(null); // or ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }

    
    
    @GetMapping("/shiftId/{shiftId}")
    public EmployeeShiftResponseDTO getEmployeeShiftByShiftId(@PathVariable Long shiftId) {
    	return employeeShiftService.getEmployeeShiftsByEmpId(shiftId);
    }
    
    @GetMapping
    public List<EmployeeShiftResponseDTO> getAllEmployeeShifts() {
    	return employeeShiftService.getAllEmployeeShifts();
    }
    
}

