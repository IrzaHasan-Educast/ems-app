package com.educast.ems.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.educast.ems.dto.LeaveRequestDTO;
import com.educast.ems.dto.LeaveResponseDTO;
import com.educast.ems.services.LeaveService;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@CrossOrigin
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @PostMapping
    public LeaveResponseDTO applyLeave(@RequestBody LeaveRequestDTO dto) {
        return leaveService.applyLeave(dto);
    }

    @PutMapping("/{id}")
    public LeaveResponseDTO updateLeave(@PathVariable Long id, @RequestBody LeaveRequestDTO dto) {
        return leaveService.updateLeave(id, dto);
    }

    @PutMapping("/{id}/approve")
    public LeaveResponseDTO approveLeave(@PathVariable Long id) {
        return leaveService.approveLeave(id);
    }

    @PutMapping("/{id}/reject")
    public LeaveResponseDTO rejectLeave(@PathVariable Long id) {
        return leaveService.rejectLeave(id);
    }

    @GetMapping
    public List<LeaveResponseDTO> getAllLeaves() {
        return leaveService.getAllLeaves();
    }

    @GetMapping("/employee/{empId}")
    public List<LeaveResponseDTO> getLeavesByEmployee(@PathVariable Long empId) {
        return leaveService.getLeavesByEmployee(empId);
    }

    @GetMapping("/status/{status}")
    public List<LeaveResponseDTO> getLeavesByStatus(@PathVariable String status) {
        return leaveService.getLeavesByStatus(status);
    }
}
