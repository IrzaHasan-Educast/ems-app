package com.educast.ems.dto;

import lombok.Data;

@Data
public class EmployeeShiftResponseDTO {
	Long id;
	private Long empId;
	private String empName;
	private Long shiftId;
	private String shiftName;
	
}
