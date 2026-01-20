package com.educast.ems.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.educast.ems.dto.ShiftRequestDTO;
import com.educast.ems.dto.ShiftResponseDTO;
import com.educast.ems.models.Employee;
import com.educast.ems.models.Shifts;
import com.educast.ems.repositories.EmployeeRepository;
import com.educast.ems.repositories.ShiftsRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShiftServiceImpl implements ShiftService{
	private final ShiftsRepository repo;
	private final EmployeeShiftService employeeShiftService;
	private final EmployeeRepository empRepo;
	

	@Override
	public void addShift(ShiftRequestDTO dto) {
		Shifts shifts = new Shifts();
		shifts.setShiftName(dto.getShiftName());
		shifts.setStartsAt(dto.getStartsAt());
		shifts.setEndsAt(dto.getEndsAt());
		if(dto.getManagerId() != null){
	        Employee manager = empRepo.findById(dto.getManagerId())
	        		.orElseThrow(()-> new RuntimeException("Id not found "));
	        shifts.setManager(manager);
	    }		repo.save(shifts);
		
	}

	@Override
	public void updateShift(Long id, ShiftRequestDTO dto) {
		Shifts updatedShift = repo.findById(id)
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
		if(dto.getManagerId()!= null) {
			Employee emp = empRepo.findById(dto.getManagerId())
					.orElseThrow(()-> new RuntimeException("Manager not found"));
			updatedShift.setManager(emp);
		}
		
		repo.save(updatedShift);
		
	}

	@Transactional
	@Override
	public void deleteShift(Long shiftId) {
//	    System.out.println("start deleting");

	    employeeShiftService.deleteEmpShiftByShiftId(shiftId);

	    Shifts deletedShift = repo.findById(shiftId)
	            .orElseThrow(() -> new RuntimeException("Id not found"));

	    repo.delete(deletedShift);

//	    System.out.println("Deletion ended");
	}


	@Override
	public List<ShiftResponseDTO> getAllShifts() {
		return repo.findAll().stream()
                .map(this::mapToDto)
                .toList();
	}
	
	@Override
	public ShiftResponseDTO getShiftById(Long id) {
		Shifts shift = repo.findById(id)
				.orElseThrow(()-> new RuntimeException("shift not found"));
		return mapToDto(shift);
	}
	
	@Override
	public ShiftResponseDTO getShiftByManagerId(Long managerId) {
		try {
		Shifts shift = repo.findByManagerId(managerId);		
			return mapToDto(shift);
		}catch (Exception e) {
			System.out.println("not a manager");
		}
		return null;
	}
	
	private ShiftResponseDTO mapToDto(Shifts shift){
		ShiftResponseDTO dto = new ShiftResponseDTO();
		dto.setId(shift.getId());
//		System.out.println(shift.getShiftName());
//		System.out.println(shift.getStartsAt());
//		System.out.println(shift.getEndsAt());
		dto.setShiftName(shift.getShiftName());
		dto.setStartsAt(shift.getStartsAt());
		dto.setEndsAt(shift.getEndsAt());
		dto.setManagerId(shift.getManager().getId());
		dto.setManagerName(shift.getManager().getFullName());
		
		return dto;
		
	}



}
