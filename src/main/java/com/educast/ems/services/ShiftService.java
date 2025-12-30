package com.educast.ems.services;

import java.util.List;

import com.educast.ems.dto.ShiftRequestDTO;
import com.educast.ems.dto.ShiftResponseDTO;

public interface ShiftService {
	
	void addShift(ShiftRequestDTO dto);
	void updateShift(Long id, ShiftRequestDTO dto);
	void deleteShift(Long shiftId);
	ShiftResponseDTO getShiftById(Long id);
	
	List<ShiftResponseDTO> getAllShifts();
	
}
