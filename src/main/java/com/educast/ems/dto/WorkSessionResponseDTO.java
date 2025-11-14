package com.educast.ems.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkSessionResponseDTO {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private Duration totalWorkingHours;
    private Duration idleTime; // optional, calculate breaks
    private List<BreakResponseDTO> breaks; // list of breaks in this session
}
