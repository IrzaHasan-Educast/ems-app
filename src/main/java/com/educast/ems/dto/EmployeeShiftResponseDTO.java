package com.educast.ems.dto;

import java.time.LocalTime;

import lombok.Data;

@Data
public class EmployeeShiftResponseDTO {
	Long id;
	private Long empId;
	private String empName;
	private Long shiftId;
	private String shiftName;
	private LocalTime startsAt;
	private LocalTime endsAt;
	
}
