package com.educast.ems.services;

import java.util.List;

import com.educast.ems.dto.EmployeeShiftRequestDTO;
import com.educast.ems.dto.EmployeeShiftResponseDTO;

public interface EmployeeShiftService {
	void assignedShift(Long empId, Long shiftId);
	void deleteAssignedShift(Long empShiftId);
	void updateAssignedShift(EmployeeShiftRequestDTO dto);
	public List<EmployeeShiftResponseDTO>  getAllEmployeeShifts();
	public EmployeeShiftResponseDTO  getEmployeeShiftsByEmpId(Long id);
	public List<EmployeeShiftResponseDTO>  getEmployeeShiftsByShiftId(Long id);

}
