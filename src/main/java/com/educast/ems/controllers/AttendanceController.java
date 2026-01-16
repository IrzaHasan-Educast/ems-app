package com.educast.ems.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.educast.ems.dto.AttendanceResponseDTO;
import com.educast.ems.security.CustomUserDetails;
import com.educast.ems.services.AttendanceService;


@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;
    

    // Mark attendance for an employee
    @PostMapping("/mark")
    public String markAttendance(@AuthenticationPrincipal CustomUserDetails userDetails) {
    	Long employeeId = userDetails.getEmployeeId();
        attendanceService.createAttendance(employeeId);
        return "Attendance marked successfully!";
    }

    // Get all attendance
    @GetMapping("/all")
    public List<AttendanceResponseDTO> getAllAttendance() {
        return attendanceService.getAllAttendance();
    }
    
    @GetMapping("/my")
    public List<AttendanceResponseDTO> getAttendanceByEmployeeId(@AuthenticationPrincipal CustomUserDetails userDetails){
    	Long id = userDetails.getEmployeeId();
        return attendanceService.getAttendanceByEmpId(id);
    }

 //  get absent today 
    @GetMapping("/absent/today")
    public List<AttendanceResponseDTO> getAbsentToday() {
        return attendanceService.getAbsentToday();
    }

    // get absent history (inactive or from joining date)
    @GetMapping("/absent/history")
    public List<AttendanceResponseDTO> getAbsentHistory() {
        return attendanceService.getAbsentEmployeesHistory();
    }
    
    // for manager to view employees attendance under their shifts
    @GetMapping("/manager/history")
    public List<AttendanceResponseDTO> getManagerAttendanceHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long managerId = userDetails.getEmployeeId();
        return attendanceService.getManagerAttendanceHistory(managerId);
    }

}
