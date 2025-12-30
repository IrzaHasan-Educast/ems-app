package com.educast.ems.services;

import com.educast.ems.dto.EmployeeShiftRequestDTO;
import com.educast.ems.dto.EmployeeShiftResponseDTO;

public interface EmployeeShiftService {
	void assignedShift(Long empId, Long shiftId);
	void deleteAssignedShift(Long empShiftId);
	void updateAssignedShift(EmployeeShiftRequestDTO dto);
	public EmployeeShiftResponseDTO  getAllEmployeeShifts();

}
