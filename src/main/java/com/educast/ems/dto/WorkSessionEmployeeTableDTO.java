package com.educast.ems.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkSessionEmployeeTableDTO {

    private Long id;
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;
    private Double totalHours;
    private Double idleHours;
    private Double totalSessionHours;
    private String status;
}

