package com.educast.ems.services;

import java.util.List;

import com.educast.ems.dto.AttendanceResponseDTO;

public interface AttendanceService {

	void createAttendance(Long id);
	
	List<AttendanceResponseDTO> getAllAttendance();
	List<AttendanceResponseDTO> getAttendanceByEmpId(Long employeeId);

//	Shift getShift(LocalTime time);
}
