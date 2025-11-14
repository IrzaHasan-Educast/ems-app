package com.educast.ems.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BreakResponseDTO {

    private Long id;
    private LocalDateTime breakStart;
    private LocalDateTime breakEnd;
    private Duration breakDuration;
}
