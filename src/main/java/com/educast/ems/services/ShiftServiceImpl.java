package com.educast.ems.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.educast.ems.dto.ShiftRequestDTO;
import com.educast.ems.dto.ShiftResponseDTO;
import com.educast.ems.models.Shifts;
import com.educast.ems.repositories.ShiftsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShiftServiceImpl implements ShiftService{
	private ShiftsRepository repo;
	

	@Override
	public void addShift(ShiftRequestDTO dto) {
		Shifts shifts = new Shifts();
		shifts.setShiftName(dto.getShiftName());
		shifts.setStartsAt(dto.getStartsAt());
		shifts.setEndsAt(dto.getEndsAt());
		repo.save(shifts);
		
	}

	@Override
	public void updateShift(ShiftRequestDTO dto) {
		Shifts updatedShift = repo.findById(dto.getId())
				.orElseThrow(()-> new RuntimeException("Id not found"));
		if(dto.getShiftName()!= null) {
			updatedShift.setShiftName(dto.getShiftName());
		}
		if(dto.getStartsAt()!=null) {
			updatedShift.setStartsAt(dto.getStartsAt());
		}
		if(dto.getEndsAt()!= null) {
			updatedShift.setEndsAt(dto.getEndsAt());
		}
		
		repo.save(updatedShift);
		
	}

	@Override
	public void deleteShift(Long shiftId) {
		Shifts deletedShift = repo.findById(shiftId)
				.orElseThrow(()-> new RuntimeException("Id not found"));
		repo.delete(deletedShift);
		
	}

	@Override
	public List<ShiftResponseDTO> getAllShifts() {
		return repo.findAll().stream()
                .map(this::mapToDto)
                .toList();
	}
	
	private ShiftResponseDTO mapToDto(Shifts shift){
		ShiftResponseDTO dto = new ShiftResponseDTO();
		dto.setShiftName(shift.getShiftName());
		dto.setStartsAt(shift.getStartsAt());
		dto.setEndsAt(shift.getEndsAt());
		
		return dto;
		
	}

}
