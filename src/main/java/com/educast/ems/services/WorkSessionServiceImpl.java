package com.educast.ems.services;

import com.educast.ems.dto.WorkSessionRequestDTO;
import com.educast.ems.dto.WorkSessionResponseDTO;
import com.educast.ems.models.WorkSession;
import com.educast.ems.models.Employee;
import com.educast.ems.repositories.WorkSessionRepository;
import com.educast.ems.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkSessionServiceImpl implements WorkSessionService {

    private final WorkSessionRepository workSessionRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public WorkSessionResponseDTO clockIn(WorkSessionRequestDTO requestDTO) {

        // Check if ongoing session exists
        WorkSession ongoing = workSessionRepository
                .findFirstByEmployeeIdAndClockOutIsNullOrderByClockInDesc(requestDTO.getEmployeeId())
                .orElse(null);

        if (ongoing != null) {
            throw new RuntimeException("Already clocked in");
        }

        Employee emp = employeeRepository.findById(requestDTO.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        WorkSession session = new WorkSession();
        session.setEmployee(emp);
        session.setClockIn(LocalDateTime.now());

        WorkSession saved = workSessionRepository.save(session);

        return mapToDTO(saved);
    }

    @Override
    public WorkSessionResponseDTO clockOut(Long id) {
        WorkSession session = workSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getClockOut() != null) {
            throw new RuntimeException("Already clocked out");
        }

        LocalDateTime now = LocalDateTime.now();
        session.setClockOut(now);

        // Calculate total hours
        Duration worked = Duration.between(session.getClockIn(), now);
        session.setTotalHours(worked.toHours() + worked.toMinutesPart() / 60.0);

        // Auto mark invalid if > 9 hrs
        if (worked.toHours() > 9) {
            // You can set a flag like session.setInvalid(true) in WorkSession model
        }

        WorkSession saved = workSessionRepository.save(session);
        return mapToDTO(saved);
    }

    @Override
    public WorkSessionResponseDTO getSessionById(Long id) {
        WorkSession session = workSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        return mapToDTO(session);
    }

    @Override
    public WorkSessionResponseDTO getOngoingSessionByEmployee(Long employeeId) {
        WorkSession session = workSessionRepository
                .findFirstByEmployeeIdAndClockOutIsNullOrderByClockInDesc(employeeId)
                .orElse(null);

        return session != null ? mapToDTO(session) : null;
    }

    @Override
    public List<WorkSessionResponseDTO> getSessionsByEmployee(Long employeeId) {
        List<WorkSession> sessions = workSessionRepository.findByEmployeeIdOrderByClockInDesc(employeeId);
        return sessions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private WorkSessionResponseDTO mapToDTO(WorkSession session) {
        WorkSessionResponseDTO dto = new WorkSessionResponseDTO();
        dto.setId(session.getId());
        dto.setEmployeeId(session.getEmployee().getId());
        dto.setEmployeeName(session.getEmployee().getFullName());
        dto.setClockInTime(session.getClockIn());
        dto.setClockOutTime(session.getClockOut());
        dto.setTotalWorkingHours(session.getTotalHours() != null
                ? Duration.ofMinutes((long)(session.getTotalHours() * 60))
                : null);
        dto.setIdleTime(null);
        dto.setBreaks(List.of());
        return dto;
    }
}
