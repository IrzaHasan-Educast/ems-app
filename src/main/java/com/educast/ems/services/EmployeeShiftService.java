package com.educast.ems.services;

import com.educast.ems.dto.EmployeeShiftRequestDTO;

public interface EmployeeShiftService {
	void assignedShift(Long empId, Long shiftId);
	void deleteAssignedShift(Long empShiftId);
	void updateAssignedShift(EmployeeShiftRequestDTO dto);
}
