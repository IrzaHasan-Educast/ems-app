package com.educast.ems.services;

import java.util.List;

import com.educast.ems.dto.ShiftRequestDTO;
import com.educast.ems.dto.ShiftResponseDTO;

public interface ShiftService {
	
	void addShift(ShiftRequestDTO dto);
	void updateShift(ShiftRequestDTO dto);
	void deleteShift(Long shiftId);
	
	List<ShiftResponseDTO> getAllShifts();
	
}
