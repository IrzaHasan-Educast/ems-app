package com.educast.ems.dto;

import java.time.LocalTime;

import lombok.Data;

@Data
public class ShiftRequestDTO {
	private Long id;
	private String shiftName;
	private LocalTime startsAt;
	private LocalTime endsAt;
    private Long managerId; // ✅ manager selected from dropdown


}
