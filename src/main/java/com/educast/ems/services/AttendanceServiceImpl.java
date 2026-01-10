package com.educast.ems.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.educast.ems.dto.AttendanceResponseDTO;
import com.educast.ems.models.Attendance;
import com.educast.ems.models.Employee;
import com.educast.ems.models.Shift;
import com.educast.ems.repositories.AttendanceRepository;
import com.educast.ems.repositories.EmployeeRepository;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void createAttendance(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Karachi"));

        // Prevent duplicate attendance for today
        boolean alreadyMarked = attendanceRepository.existsByEmployeeIdAndAttendanceDate(id, today);
        if (alreadyMarked) {
            throw new RuntimeException("Attendance already marked for today");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(emp);
        attendance.setAttendanceDate(today);
        LocalTime time = LocalTime.now(ZoneId.of("Asia/Karachi"));
        attendance.setAttendanceTime(time);
        attendance.setShift(getShift(time));
        attendance.setPresent(true);

        attendanceRepository.save(attendance);
    }

    private Shift getShift(LocalTime time) {
        // Morning first half: 08:00 - 12:59
        if (!time.isBefore(LocalTime.of(8, 0)) && time.isBefore(LocalTime.of(13, 0))) {
            return Shift.MORNING;
        } 
        // Morning second half: 13:00 - 16:59
        else if (!time.isBefore(LocalTime.of(13, 0)) && time.isBefore(LocalTime.of(17, 0))) {
            return Shift.MORNING_SECOND_HALF;
        } 
        // Night first half: 20:00 - 23:59
        else if (!time.isBefore(LocalTime.of(20, 0))) {
            return Shift.NIGHT;
        } 
        // Night second half: 00:00 - 04:59
        else if (time.isBefore(LocalTime.of(5, 0))) {
            return Shift.NIGHT_SECOND_HALF;
        } 
        // Other random times
        else {
            return Shift.CUSTOM;
        }
    }


    @Override
    public List<AttendanceResponseDTO> getAllAttendance() {
        List<Attendance> attendances = attendanceRepository.findAll();
        return attendances.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    

    @Override
    public List<AttendanceResponseDTO> getAttendanceByEmpId(Long employeeId) {
        List<Attendance> empAttendances = attendanceRepository.findByEmployeeId(employeeId);

        // Return empty list if no attendance found
        return empAttendances.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    // Get active employees absent today
    @Override
    public List<AttendanceResponseDTO> getAbsentToday() {
        LocalDate today = LocalDate.now();
        List<Employee> absentEmployees = attendanceRepository.findActiveEmployeesAbsentOn(today);
        List<AttendanceResponseDTO> response = new ArrayList<>();
        for (Employee e : absentEmployees) {
            AttendanceResponseDTO dto = new AttendanceResponseDTO();
            dto.setEmployeeId(e.getId());
            dto.setEmployeeName(e.getFullName());
            dto.setAttendanceDate(today);
            dto.setPresent(false);
            response.add(dto);
        }
        return response;
    }

    // Get absent employees for inactive ones up to leaving date
    @Override
    public List<AttendanceResponseDTO> getAbsentEmployeesHistory() {
        LocalDate today = LocalDate.now();
        List<Employee> absentEmployees = attendanceRepository.findEmployeesAbsentBetween(today);
        List<AttendanceResponseDTO> response = new ArrayList<>();
        for (Employee e : absentEmployees) {
            AttendanceResponseDTO dto = new AttendanceResponseDTO();
            dto.setEmployeeId(e.getId());
            dto.setEmployeeName(e.getFullName());
            dto.setPresent(false);
            response.add(dto);
        }
        return response;
    }

    private AttendanceResponseDTO mapToDto(Attendance attendance) {
        AttendanceResponseDTO dto = new AttendanceResponseDTO();
        dto.setEmployeeId(attendance.getEmployee().getId());
        dto.setEmployeeName(attendance.getEmployee().getFullName()); 
        dto.setAttendanceDate(attendance.getAttendanceDate());
        dto.setAttendanceTime(attendance.getAttendanceTime());
        dto.setPresent(attendance.isPresent());
        dto.setShift(attendance.getShift());
        return dto;
    }
    
    @Override
    public List<AttendanceResponseDTO> getManagerAttendanceHistory(Long managerId) {

        List<Attendance> list =
                attendanceRepository.findAttendanceHistoryForManager(managerId);

        return list.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

}
