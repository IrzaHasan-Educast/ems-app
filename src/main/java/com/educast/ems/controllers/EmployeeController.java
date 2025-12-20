package com.educast.ems.controllers;

import com.educast.ems.dto.EmployeeRequest;
import com.educast.ems.dto.EmployeeResponse;
import com.educast.ems.models.Employee;
import com.educast.ems.services.EmployeeService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public List<EmployeeResponse> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR') or principal == #id")
    public EmployeeResponse getEmployee(@PathVariable Long id) {
        EmployeeResponse emp = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return emp;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public EmployeeResponse createEmployee(@RequestBody EmployeeRequest dto) {
        Employee employee = new Employee();
        employee.setFullName(dto.getFullName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setGender(dto.getGender());
        employee.setDepartment(dto.getDepartment());
        employee.setDesignation(dto.getDesignation());
        employee.setRole(dto.getRole());
        employee.setJoiningDate(dto.getJoiningDate());
        employee.setActive(dto.isActive());

        return employeeService.createEmployee(employee, dto.getUsername(), dto.getPassword());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public EmployeeResponse updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequest dto) {
        EmployeeResponse updated = employeeService.updateEmployee(id, dto);
        return updated;
    }

//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')") // only admin can delete
//    public void deleteEmployee(@PathVariable Long id) {
//        employeeService.deleteEmployee(id);
//    }
    
 // ✅ Toggle active/inactive
    @PutMapping("/toggle-active/{id}")
    public ResponseEntity<String> toggleActive(@PathVariable Long id) {
        EmployeeResponse employee = employeeService.toggleActive(id);
        String status = employee.isActive() ? "activated" : "deactivated";
        return ResponseEntity.ok("Employee " + status + " successfully");
    }


}

