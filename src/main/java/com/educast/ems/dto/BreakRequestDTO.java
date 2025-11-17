package com.educast.ems.dto;

import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BreakRequestDTO {

    private Long workSessionId; // kaunse session me break hai
    private LocalDateTime startTime;
    private LocalDateTime endTime; // optional, break end time ke liye
    private Duration durationHours; // can be null until break ends

}
