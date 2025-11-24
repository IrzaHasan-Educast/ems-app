package com.educast.ems.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.educast.ems.models.Shift;

import lombok.Data;

@Data
public class AttendanceResponseDTO {
	
	private Long employeeId;
	private String employeeName;
	private boolean present;
	private LocalTime attendanceTime;
	private LocalDate attendanceDate;
	private Shift shift;
	
	

}
