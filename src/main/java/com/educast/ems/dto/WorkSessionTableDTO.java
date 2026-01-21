package com.educast.ems.dto;

import java.time.LocalDateTime;

import lombok.Getter;
@Getter
public class WorkSessionTableDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    String role;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private Double totalWorkingHours;
    private Double idleHours;
    private Double totalSessionHours;
    private String status;

    public WorkSessionTableDTO(Long id,Long empId, String employeeName, String role,
                               LocalDateTime clockInTime, LocalDateTime clockOutTime,
                               Double totalWorkingHours, Double idleHours,
                               Double totalSessionHours, String status) {
        this.id = id;
        this.employeeId = empId;
        this.employeeName = employeeName;
        this.role = role;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
        this.totalWorkingHours = totalWorkingHours;
        this.idleHours = idleHours;
        this.totalSessionHours = totalSessionHours;
        this.status = status;
    }
}
