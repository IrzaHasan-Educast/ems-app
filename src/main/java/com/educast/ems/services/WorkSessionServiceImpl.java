package com.educast.ems.services;

import com.educast.ems.dto.BreakResponseDTO;
import com.educast.ems.dto.WorkSessionRequestDTO;
import com.educast.ems.dto.WorkSessionResponseDTO;
import com.educast.ems.models.WorkSession;
import com.educast.ems.models.Employee;
import com.educast.ems.models.Break;
import com.educast.ems.repositories.WorkSessionRepository;
import com.educast.ems.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkSessionServiceImpl implements WorkSessionService {

    private final WorkSessionRepository workSessionRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Return all sessions sorted by clockIn desc (latest first).
     */
    @Override
    public List<WorkSessionResponseDTO> getAllSessions() {
        return workSessionRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(WorkSession::getClockIn, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public WorkSessionResponseDTO clockIn(WorkSessionRequestDTO requestDTO) {

        // Check if ongoing session exists (employee already clocked in)
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
        session.setClockIn(LocalDateTime.now(ZoneId.of("Asia/Karachi")));
//        System.out.println(LocalDateTime.now(ZoneOffset.UTC));
        session.setStatus("Working");

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
	
	    LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Karachi"));
	
	    // End any ongoing breaks for this session (set endTime + duration)
	    endOngoingBreaks(session, now);
	
	    session.setClockOut(now);
	
	    // Calculate raw worked duration (clockOut - clockIn) in seconds
	    Duration rawWorked = Duration.between(session.getClockIn(), now);
	    long rawSeconds = rawWorked.getSeconds();
	
	    // Sum breaks seconds (if any)
	    long totalBreakSeconds = 0;
	    if (session.getBreaks() != null && !session.getBreaks().isEmpty()) {
	        totalBreakSeconds = session.getBreaks().stream()
	                .mapToLong(br -> {
	                    if (br.getStartTime() != null) {
	                        LocalDateTime end = br.getEndTime() != null ? br.getEndTime() : now;
	                        return Duration.between(br.getStartTime(), end).getSeconds();
	                    } else {
	                        return 0L;
	                    }
	                })
	                .sum();
	    }
	
	    // Net working seconds = raw - breaks (floor at 0)
	    long netWorkingSeconds = Math.max(0L, rawSeconds - totalBreakSeconds);
	
	    // 1️⃣ Store totalHours as fractional hours (seconds -> hours double) (existing behavior)
	    double totalHours = netWorkingSeconds / 3600.0;
	    session.setTotalHours(totalHours);
	
	    // 2️⃣ Save total session hours (clockOut - clockIn, including breaks)
	    double totalSessionHours = rawSeconds / 3600.0;
	    session.setTotalSessionHours(totalSessionHours);
	
	    // 3️⃣ Save idle hours (breaks total)
	    double idleHours = totalBreakSeconds / 3600.0;
	    session.setIdleHours(idleHours);
	
	    // Determine status based on net working hours (after breaks)
	    if (totalHours < 3.0) {
	        session.setStatus("Early Clocked Out");
	    } else if (totalHours > 9.0) {
	        session.setStatus("Invalid Clocked Out");
	    } else {
	        session.setStatus("Completed");
	    }
	
	    WorkSession saved = workSessionRepository.save(session);
	    return mapToDTO(saved);
	}


    @Override
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
    
    @Override
    public List<WorkSessionResponseDTO> getAllSessionsFiltered(
            Long employeeId, String status, Integer month, String searchTerm, int page, int size) {

        List<WorkSession> sessions = workSessionRepository.findFiltered(employeeId, status, month);

        // Optional search term filtering in memory (small overhead)
        if (searchTerm != null && !searchTerm.isBlank()) {
            String lower = searchTerm.toLowerCase();
            sessions = sessions.stream()
                    .filter(s -> s.getEmployee().getFullName().toLowerCase().contains(lower)
                            || (s.getStatus() != null && s.getStatus().toLowerCase().contains(lower)))
                    .toList();
        }

        // Pagination
        int start = page * size;
        int end = Math.min(start + size, sessions.size());
        if (start > end) return List.of();

        List<WorkSession> pageList = sessions.subList(start, end);

        // Map to DTO, compute duration for ongoing Working sessions
        return pageList.stream().map(ws -> {
            WorkSessionResponseDTO dto = mapToDTO(ws);
            if ("Working".equals(ws.getStatus())) {
                LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Karachi"));
                long totalMillis = Duration.between(ws.getClockIn(), now).toMillis();

                long breakMillis = ws.getBreaks() != null ? ws.getBreaks().stream()
                        .mapToLong(br -> {
                            LocalDateTime breakEnd = br.getEndTime() != null ? br.getEndTime() : now;
                            return Duration.between(br.getStartTime(), breakEnd).toMillis();
                        }).sum() : 0;

                long netMillis = Math.max(0, totalMillis - breakMillis);

                dto.setTotalWorkingHours(Duration.ofMillis(netMillis));
            }
            return dto;
        }).toList();
    }


    /**
     * Map WorkSession entity -> WorkSessionResponseDTO
     * Ensures Duration fields are created from net seconds (totalHours double).
     */
    private WorkSessionResponseDTO mapToDTO(WorkSession session) {
        WorkSessionResponseDTO dto = new WorkSessionResponseDTO();
        dto.setId(session.getId());
        dto.setEmployeeId(session.getEmployee().getId());
        dto.setEmployeeName(session.getEmployee().getFullName());
        dto.setClockInTime(session.getClockIn());
        dto.setClockOutTime(session.getClockOut());
        dto.setStatus(session.getStatus());

        // Convert stored totalHours (double hours) -> Duration (preserve fractional)
        if (session.getTotalHours() != null) {
            long seconds = (long) Math.round(session.getTotalHours() * 3600.0);
            dto.setTotalWorkingHours(Duration.ofSeconds(seconds));
        } else {
            dto.setTotalWorkingHours(null);
        }

        // Total session hours (clockOut - clockIn, including breaks)
        if (session.getTotalSessionHours() != null) {
            long seconds = (long) Math.round(session.getTotalSessionHours() * 3600.0);
            dto.setTotalSessionHours(Duration.ofSeconds(seconds));
        } else {
            dto.setTotalSessionHours(null);
        }

        // Idle time / total break duration
        if (session.getIdleHours() != null) {
            long seconds = (long) Math.round(session.getIdleHours() * 3600.0);
            dto.setIdleTime(Duration.ofSeconds(seconds));
        } else {
            dto.setIdleTime(null);
        }

        // Breaks -> BreakResponseDTO
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
                    }).collect(Collectors.toList());
            dto.setBreaks(breakDTOs);

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

        dto.setIdleTime(null); // optional: compute if you track idle separately
        return dto;
    }

    /**
     * End any ongoing breaks by setting endTime and durationHours.
     * Because WorkSession has cascade = ALL on breaks, saving the session later will persist those changes.
     */
    private void endOngoingBreaks(WorkSession session, LocalDateTime endTime) {
        if (session.getBreaks() != null) {
            for (Break b : session.getBreaks()) {
                if (b.getEndTime() == null) {
                    b.setEndTime(endTime);
                    // compute duration in hours as double
                    long seconds = Duration.between(b.getStartTime(), endTime).getSeconds();
                    double hours = seconds / 3600.0;
                    b.setDurationHours(hours);
                }
            }
        }
    }
}
