package com.educast.ems.dto;

import lombok.Data;

@Data
public class EmployeeShiftRequestDTO {
	private Long id;
	private Long employeeId;
	private Long shiftId;

}
