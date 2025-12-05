package com.educast.ems.dto;

import java.time.LocalDate;

import com.educast.ems.models.LeaveType;

import lombok.Data;

@Data
public class LeaveRequestDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    private int duration;
    private String description;
    private String prescriptionImg;
    private LeaveType leaveType;
    private Long employeeId;

    // getters & setters
}
