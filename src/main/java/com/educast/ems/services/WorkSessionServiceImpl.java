package com.educast.ems.services;

import com.educast.ems.dto.BreakResponseDTO;
import com.educast.ems.dto.WorkSessionHoursSyncDTO;
import com.educast.ems.dto.WorkSessionRequestDTO;
import com.educast.ems.dto.WorkSessionResponseDTO;
import com.educast.ems.models.Break;
import com.educast.ems.models.Employee;
import com.educast.ems.models.WorkSession;
import com.educast.ems.repositories.EmployeeRepository;
import com.educast.ems.repositories.WorkSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkSessionServiceImpl implements WorkSessionService {

    private static final ZoneId PK_ZONE = ZoneId.of("Asia/Karachi");

    private final WorkSessionRepository workSessionRepository;
    private final EmployeeRepository employeeRepository;

    /* ================= CLOCK IN ================= */

    @Override
    public WorkSessionResponseDTO clockIn(WorkSessionRequestDTO requestDTO) {

        if (workSessionRepository
                .findFirstByEmployeeIdAndClockOutIsNullOrderByClockInDesc(
                        requestDTO.getEmployeeId())
                .isPresent()) {
            throw new RuntimeException("Already clocked in");
        }

        Employee emp = employeeRepository.findById(requestDTO.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        WorkSession session = new WorkSession();
        session.setEmployee(emp);
        session.setClockIn(now());
        session.setStatus("Working");

        return mapToDTO(workSessionRepository.save(session));
    }

    /* ================= CLOCK OUT ================= */

    @Override
    public WorkSessionResponseDTO clockOut(Long id) {

        WorkSession session = workSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getClockOut() != null) {
            throw new RuntimeException("Already clocked out");
        }

        LocalDateTime now = now();
        endOngoingBreaks(session, now);
        session.setClockOut(now);

        Duration rawDuration = Duration.between(session.getClockIn(), now);
        long breakSeconds = calculateBreakSeconds(session, now);
        long netSeconds = Math.max(0, rawDuration.getSeconds() - breakSeconds);

        session.setTotalSessionHours(toHours(rawDuration.getSeconds()));
        session.setIdleHours(toHours(breakSeconds));
        session.setTotalHours(toHours(netSeconds));
        session.setStatus(resolveStatus(session.getTotalHours()));

        return mapToDTO(workSessionRepository.save(session));
    }

    /* ================= FETCH ================= */

    @Override
    public Optional<WorkSessionResponseDTO> getActiveSession(Long empId) {
        return workSessionRepository
                .findByEmployeeIdAndClockOutIsNull(empId)
                .map(this::mapToDTO);
    }

    @Override
    public WorkSessionResponseDTO getSessionById(Long id) {
        return workSessionRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    @Override
    public WorkSessionResponseDTO getOngoingSessionByEmployee(Long employeeId) {
        return workSessionRepository
                .findFirstByEmployeeIdAndClockOutIsNullOrderByClockInDesc(employeeId)
                .map(this::mapToDTO)
                .orElse(null);
    }

    @Override
    public List<WorkSessionResponseDTO> getSessionsByEmployee(Long employeeId) {
        return workSessionRepository
                .findByEmployeeIdOrderByClockInDesc(employeeId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<WorkSessionResponseDTO> getAllSessions() {
        return workSessionRepository.findAll().stream()
                .sorted(Comparator.comparing(
                        WorkSession::getClockIn,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .map(this::mapToDTO)
                .toList();
    }

    /* ================= FILTERED ================= */

    @Override
    public List<WorkSessionResponseDTO> getAllSessionsFiltered(
            Long employeeId,
            String status,
            Integer month,
            String searchTerm,
            int page,
            int size) {

        LocalDateTime now = now();

        List<WorkSession> sessions =
                workSessionRepository.findFiltered(employeeId, status, month);

        // 🔍 Search filter
        if (searchTerm != null && !searchTerm.isBlank()) {
            String lower = searchTerm.toLowerCase();
            sessions = sessions.stream()
                    .filter(s ->
                            s.getEmployee().getFullName().toLowerCase().contains(lower)
                                    || (s.getStatus() != null &&
                                        s.getStatus().toLowerCase().contains(lower))
                    )
                    .toList();
        }

        // 📄 Pagination
        int start = page * size;
        int end = Math.min(start + size, sessions.size());
        if (start >= sessions.size()) return List.of();

        return sessions.subList(start, end)
                .stream()
                .map(ws -> mapFilteredDTO(ws, now))
                .toList();
    }

    /* ================= HELPERS ================= */

    private LocalDateTime now() {
        return LocalDateTime.now(PK_ZONE);
    }

    private long calculateBreakSeconds(WorkSession session, LocalDateTime now) {
        if (session.getBreaks() == null) return 0;

        return session.getBreaks().stream()
                .filter(b -> b.getStartTime() != null)
                .mapToLong(b ->
                        Duration.between(
                                b.getStartTime(),
                                b.getEndTime() != null ? b.getEndTime() : now
                        ).getSeconds()
                )
                .sum();
    }

    private void endOngoingBreaks(WorkSession session, LocalDateTime endTime) {
        if (session.getBreaks() == null) return;

        session.getBreaks().stream()
                .filter(b -> b.getEndTime() == null)
                .forEach(b -> {
                    b.setEndTime(endTime);
                    b.setDurationHours(
                            Duration.between(b.getStartTime(), endTime)
                                    .toSeconds() / 3600.0
                    );
                });
    }

    private String resolveStatus(double totalHours) {
        if (totalHours < 3) return "Early Clocked Out";
        if (totalHours > 9) return "Invalid Clocked Out";
        return "Completed";
    }

    private double toHours(long seconds) {
        return seconds / 3600.0;
    }

    private WorkSessionResponseDTO mapFilteredDTO(
            WorkSession session,
            LocalDateTime now) {

        WorkSessionResponseDTO dto = mapToDTO(session);

        if ("Working".equals(session.getStatus())) {
            long rawSeconds =
                    Duration.between(session.getClockIn(), now).getSeconds();
            long breakSeconds = calculateBreakSeconds(session, now);
            long netSeconds = Math.max(0, rawSeconds - breakSeconds);
            dto.setTotalWorkingHours(Duration.ofSeconds(netSeconds));
        }

        return dto;
    }
    
    @Override
    public WorkSessionResponseDTO syncSessionHours(
            Long sessionId,
            WorkSessionHoursSyncDTO dto
    ) {

        WorkSession session = workSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // 🔒 safety checks
        if (session.getClockOut() == null) {
            throw new RuntimeException("Cannot sync an active session");
        }

        if (dto.getTotalWorkingHours() == null ||
            dto.getTotalSessionHours() == null) {
            throw new RuntimeException("Invalid duration data");
        }

        // 🧮 convert Duration → hours
        double totalHours =
                dto.getTotalWorkingHours().getSeconds() / 3600.0;

        double totalSessionHours =
                dto.getTotalSessionHours().getSeconds() / 3600.0;

        double idleHours =
                dto.getIdleTime() != null
                        ? dto.getIdleTime().getSeconds() / 3600.0
                        : 0;

        session.setTotalHours(totalHours);
        session.setIdleHours(idleHours);
        session.setTotalSessionHours(totalSessionHours);
        session.setStatus(resolveStatus(totalHours));

        System.out.println("Hello World!");
        return mapToDTO(workSessionRepository.save(session));
    }


    /* ================= DTO MAPPING ================= */

    private WorkSessionResponseDTO mapToDTO(WorkSession session) {

        WorkSessionResponseDTO dto = new WorkSessionResponseDTO();
        dto.setId(session.getId());
        dto.setEmployeeId(session.getEmployee().getId());
        dto.setEmployeeName(session.getEmployee().getFullName());
        dto.setClockInTime(session.getClockIn());
        dto.setClockOutTime(session.getClockOut());
        dto.setStatus(session.getStatus());

        if (session.getTotalHours() != null) {
            dto.setTotalWorkingHours(
                    Duration.ofSeconds(
                            Math.round(session.getTotalHours() * 3600)
                    )
            );
        }

        if (session.getTotalSessionHours() != null) {
            dto.setTotalSessionHours(
                    Duration.ofSeconds(
                            Math.round(session.getTotalSessionHours() * 3600)
                    )
            );
        }

        if (session.getIdleHours() != null) {
            dto.setIdleTime(
                    Duration.ofSeconds(
                            Math.round(session.getIdleHours() * 3600)
                    )
            );
        }

        if (session.getBreaks() != null && !session.getBreaks().isEmpty()) {

            List<BreakResponseDTO> breakDTOs =
                    session.getBreaks().stream().map(br -> {
                        BreakResponseDTO b = new BreakResponseDTO();
                        b.setId(br.getId());
                        b.setStartTime(br.getStartTime());
                        b.setEndTime(br.getEndTime());
                        if (br.getStartTime() != null && br.getEndTime() != null) {
                            b.setBreakDuration(
                                    Duration.between(
                                            br.getStartTime(),
                                            br.getEndTime()
                                    )
                            );
                        }
                        return b;
                    }).toList();

            dto.setBreaks(breakDTOs);

            breakDTOs.stream()
                    .filter(b -> b.getEndTime() == null)
                    .findFirst()
                    .ifPresent(b -> {
                        dto.setOnBreak(true);
                        dto.setCurrentBreakId(b.getId());
                    });

        } else {
            dto.setBreaks(List.of());
            dto.setOnBreak(false);
        }

        return dto;
    }
}
