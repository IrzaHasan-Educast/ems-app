package com.educast.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkSessionHoursSyncDTO {

    private Duration totalWorkingHours;
    private Duration idleTime;
    private Duration totalSessionHours;
}
