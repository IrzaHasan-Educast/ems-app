package com.educast.ems.services;

import org.springframework.stereotype.Service;

import com.educast.ems.dto.EmployeeShiftRequestDTO;
import com.educast.ems.dto.EmployeeShiftResponseDTO;
import com.educast.ems.models.Employee;
import com.educast.ems.models.EmployeeShift;
import com.educast.ems.models.Shifts;
import com.educast.ems.repositories.EmployeeRepository;
import com.educast.ems.repositories.EmployeeShiftRepository;
import com.educast.ems.repositories.ShiftsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeShiftServiceImpl implements EmployeeShiftService {

    private final EmployeeShiftRepository employeeShiftRepo;
    private final EmployeeRepository employeeRepo;
    private final ShiftsRepository shiftsRepo;
	@Override
	
		public EmployeeShiftResponseDTO getAllEmployeeShifts() {
		
			return null;
		}
    @Override
    public void assignedShift(Long empId, Long shiftId) {

        // 1. Check employee exists
        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // 2. Check shift exists
        Shifts shift = shiftsRepo.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        // 3. Check employee already has shift
        employeeShiftRepo.findByEmployeeId(empId)
                .ifPresent(es -> {
                    throw new RuntimeException("Employee already assigned to a shift");
                });

        // 4. Assign shift
        EmployeeShift employeeShift = new EmployeeShift();
        employeeShift.setEmployee(employee);
        employeeShift.setShift(shift);

        employeeShiftRepo.save(employeeShift);
    }

    @Override
    public void deleteAssignedShift(Long empShiftId) {
        EmployeeShift employeeShift = employeeShiftRepo.findById(empShiftId)
                .orElseThrow(() -> new RuntimeException("Assigned shift not found"));

        employeeShiftRepo.delete(employeeShift);
    }

    @Override
    public void updateAssignedShift(EmployeeShiftRequestDTO dto) {

        EmployeeShift employeeShift = employeeShiftRepo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Assigned shift not found"));

        Shifts newShift = shiftsRepo.findById(dto.getShiftId())
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        employeeShift.setShift(newShift);
        employeeShiftRepo.save(employeeShift);
    }

	
}
