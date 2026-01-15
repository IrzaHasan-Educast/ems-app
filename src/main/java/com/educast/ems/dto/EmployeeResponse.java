package com.educast.ems.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private String department;
    private String designation;
    private String role;
    private LocalDate joiningDate;
    private boolean active;
    private String assignedShift;
    private String username; // linked user username
}
