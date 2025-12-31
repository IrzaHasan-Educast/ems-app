package com.educast.ems.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeRequest {
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private String department;
    private String designation;
    private String role;
    private LocalDate joiningDate;
    private boolean active;

    // User credentials
    private String username;
    private String password;
    private Long shiftId;
}
