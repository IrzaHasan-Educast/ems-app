package com.educast.ems.services;

import com.educast.ems.dto.BreakResponseDTO;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkSessionServiceImpl implements WorkSessionService {

    private final WorkSessionRepository workSessionRepository;
    private final EmployeeRepository employeeRepository;
    
    public List<WorkSessionResponseDTO> getAllSessions() {
    	return workSessionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList()); 
    }

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
        endOngoingBreaks(session,now);

        session.setClockOut(now);

        // Calculate total hours
        Duration worked = Duration.between(session.getClockIn(), now);
        session.setTotalHours(worked.toHours() + worked.toMinutesPart() +worked.toSecondsPart() / 60.0);

        // Auto mark invalid if > 9 hrs
        session.setStatus(worked.toHours() > 9 ? "Invalid Clocked Out" : "Completed");


        WorkSession saved = workSessionRepository.save(session);
        return mapToDTO(saved);
    }

    public Optional<WorkSessionResponseDTO> getActiveSession(Long empId) {
        Optional<WorkSession> session = workSessionRepository.findByEmployeeIdAndClockOutIsNull(empId);

        return session.map(this::mapToDTO);
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
        dto.setStatus(session.getStatus());
        dto.setTotalWorkingHours(session.getTotalHours() != null
                ? Duration.ofMinutes((long)(session.getTotalHours() * 60))
                : null);

        // Breaks: session.getBreaks() assume it returns List<Break>
        if (session.getBreaks() != null && !session.getBreaks().isEmpty()) {
            List<BreakResponseDTO> breakDTOs = session.getBreaks().stream()
                    .map(brk -> {
                        BreakResponseDTO b = new BreakResponseDTO();
                        b.setId(brk.getId());
                        b.setStartTime(brk.getStartTime());
                        b.setEndTime(brk.getEndTime());
                        if (brk.getStartTime() != null && brk.getEndTime() != null) {
                            b.setBreakDuration(Duration.between(brk.getStartTime(), brk.getEndTime()));
                        } else {
                            b.setBreakDuration(null);
                        }
                        return b;
                    }).toList();
            dto.setBreaks(breakDTOs);

            // Detect ongoing break
            Optional<BreakResponseDTO> ongoingBreak = breakDTOs.stream()
                    .filter(b -> b.getEndTime() == null)
                    .findFirst();
            dto.setOnBreak(ongoingBreak.isPresent());
            ongoingBreak.ifPresent(b -> dto.setCurrentBreakId(b.getId()));
        } else {
            dto.setBreaks(List.of());
            dto.setOnBreak(false);
            dto.setCurrentBreakId(null);
        }

        dto.setIdleTime(null); // existing behavior
        return dto;
    }

    private void endOngoingBreaks(WorkSession session, LocalDateTime endTime) {
        if (session.getBreaks() != null) {
            session.getBreaks().stream()
                .filter(b -> b.getEndTime() == null)
                .forEach(b -> {
                    b.setEndTime(endTime);
                    Duration duration = Duration.between(b.getStartTime(), endTime);
                    b.setDurationHours(duration.toMinutes() / 60.0);
                });
        }
    }

}
