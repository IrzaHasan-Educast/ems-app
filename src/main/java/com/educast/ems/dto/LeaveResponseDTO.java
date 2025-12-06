package com.educast.ems.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.educast.ems.models.LeaveStatus;
import com.educast.ems.models.LeaveType;

import lombok.Data;

@Data
public class LeaveResponseDTO {

    private Long id;
    private LocalDateTime appliedOn;
    private LocalDateTime updatedOn;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveStatus status;
    private LeaveType leaveType;
    private int duration;
    private String description;
    private String prescriptionImg;

}