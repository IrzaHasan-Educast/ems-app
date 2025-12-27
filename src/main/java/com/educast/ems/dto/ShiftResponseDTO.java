package com.educast.ems.dto;

import java.time.LocalTime;

import lombok.Data;

@Data
public class ShiftResponseDTO {
	private String shiftName;
	private LocalTime startsAt;
	private LocalTime endsAt;

}
