package com.educast.ems.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BreakRequestDTO {

    private Long workSessionId; // kaunse session me break hai
    private LocalDateTime breakStart;
    private LocalDateTime breakEnd; // optional, break end time ke liye
}
