package com.educast.ems.services;

import java.util.List;

import com.educast.ems.dto.LeaveRequestDTO;
import com.educast.ems.dto.LeaveResponseDTO;

public interface LeaveService {

    LeaveResponseDTO applyLeave(LeaveRequestDTO leaveRequestDTO);

    LeaveResponseDTO updateLeave(Long id, LeaveRequestDTO leaveRequestDTO);

    LeaveResponseDTO approveLeave(Long id);

    LeaveResponseDTO rejectLeave(Long id);

    List<LeaveResponseDTO> getAllLeaves();

    List<LeaveResponseDTO> getLeavesByEmployee(Long employeeId);
    
    List<LeaveResponseDTO> findEmployeeLeaveOfManagerShift(Long mangerId);

    List<LeaveResponseDTO> getLeavesByStatus(String status);
    
    boolean deleteLeave(Long id);
    
    LeaveResponseDTO pendingLeave(Long id);

}
