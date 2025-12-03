package com.educast.ems.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.educast.ems.dto.LeaveRequestDTO;
import com.educast.ems.dto.LeaveResponseDTO;
import com.educast.ems.models.Employee;
import com.educast.ems.models.Leave;
import com.educast.ems.models.LeaveStatus;
import com.educast.ems.repositories.EmployeeRepository;
import com.educast.ems.repositories.LeaveRepository;

@Service
public class LeaveServiceImpl implements LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public LeaveResponseDTO applyLeave(LeaveRequestDTO dto) {

        Employee emp = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow();

        Leave leave = new Leave();
        leave.setEmployee(emp);
        leave.setStartDate(dto.getStartDate());
        leave.setEndDate(dto.getEndDate());
        leave.setDuration(dto.getDuration());
        leave.setDescription(dto.getDescription());
        leave.setPrescriptionImg(dto.getPrescriptionImg());
        leave.setLeaveType(dto.getLeaveType());

        leave.setStatus(LeaveStatus.PENDING);
        leave.setAppliedOn(LocalDateTime.now(ZoneId.of("Asia/Karachi")));

        Leave saved = leaveRepository.save(leave);
        return mapToResponse(saved);
    }

    @Override
    public LeaveResponseDTO updateLeave(Long id, LeaveRequestDTO dto) {
        Leave existing = leaveRepository.findById(id).orElseThrow();

        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setDuration(dto.getDuration());
        existing.setDescription(dto.getDescription());
        existing.setPrescriptionImg(dto.getPrescriptionImg());
        existing.setLeaveType(dto.getLeaveType());
        existing.setUpdatedOn(LocalDateTime.now(ZoneId.of("Asia/Karachi")));

        return mapToResponse(leaveRepository.save(existing));
    }

    @Override
    public LeaveResponseDTO approveLeave(Long id) {
        Leave leave = leaveRepository.findById(id).orElseThrow();
        leave.setStatus(LeaveStatus.APPROVED);
        leave.setUpdatedOn(LocalDateTime.now(ZoneId.of("Asia/Karachi")));
        return mapToResponse(leaveRepository.save(leave));
    }

    @Override
    public LeaveResponseDTO rejectLeave(Long id) {
        Leave leave = leaveRepository.findById(id).orElseThrow();
        leave.setStatus(LeaveStatus.REJECTED);
        leave.setUpdatedOn(LocalDateTime.now(ZoneId.of("Asia/Karachi")));
        return mapToResponse(leaveRepository.save(leave));
    }

    @Override
    public List<LeaveResponseDTO> getAllLeaves() {
        return leaveRepository.findAll()
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveResponseDTO> getLeavesByEmployee(Long employeeId) {
        return leaveRepository.findByEmployeeId(employeeId)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveResponseDTO> getLeavesByStatus(String status) {
        return leaveRepository.findByStatus(LeaveStatus.valueOf(status.toUpperCase()))
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private LeaveResponseDTO mapToResponse(Leave leave) {
        LeaveResponseDTO r = new LeaveResponseDTO();
        r.setId(leave.getId());
        r.setAppliedOn(leave.getAppliedOn());
        r.setUpdatedOn(leave.getUpdatedOn());
        r.setStatus(leave.getStatus());
        r.setLeaveType(leave.getLeaveType());
        r.setDuration(leave.getDuration());
        r.setDescription(leave.getDescription());
        r.setPrescriptionImg(leave.getPrescriptionImg());
        return r;
    }

}
