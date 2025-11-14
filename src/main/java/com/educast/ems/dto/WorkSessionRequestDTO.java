package com.educast.ems.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkSessionRequestDTO {

    private Long employeeId;           // kaun employee ki session hai
    private LocalDateTime clockInTime; // optional: clock in ke liye
    private LocalDateTime clockOutTime; // optional: clock out ke liye
}
