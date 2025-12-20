package com.educast.ems.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.educast.ems.models.Break;
import com.educast.ems.models.WorkSession;
import com.educast.ems.repositories.WorkSessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkSessionAutoClockOutService {

    private final WorkSessionRepository workSessionRepository;
    private static final ZoneId PK_ZONE = ZoneId.of("Asia/Karachi");

    @Transactional
    public void autoClockOutSession(WorkSession session) {
        LocalDateTime now = LocalDateTime.now(PK_ZONE);

        // End ongoing breaks
        List<Break> breaks = session.getBreaks();

        if (breaks != null) {
            breaks.stream()
                .filter(b -> b.getEndTime() == null)
                .forEach(b -> {
                    b.setEndTime(now);
                    b.setDurationHours(
                        Duration.between(b.getStartTime(), now).toSeconds() / 3600.0
                    );
                });
        }

        

        // Calculate total and net hours
        long totalSeconds = Duration.between(session.getClockIn(), now).getSeconds();
        long breakSeconds = session.getBreaks() == null ? 0 :
                session.getBreaks().stream()
                        .filter(b -> b.getStartTime() != null)
                        .mapToLong(b -> Duration.between(b.getStartTime(),
                                b.getEndTime() != null ? b.getEndTime() : now).getSeconds())
                        .sum();
    	System.out.println("runs here");

        long netSeconds = Math.max(0, totalSeconds - breakSeconds);

        session.setTotalSessionHours(totalSeconds / 3600.0);
        session.setIdleHours(breakSeconds / 3600.0);
        session.setTotalHours(netSeconds / 3600.0);

        // Auto clock-out status
        session.setClockOut(now);
        session.setStatus("Auto Clocked Out");

        workSessionRepository.save(session);
    }

    // Fetch sessions in rolling window
    public List<WorkSession> getSessionsForAutoClockOut(LocalDateTime windowStart) {
        return workSessionRepository.findByClockOutIsNullAndClockInAfter(windowStart);
    }
    
//    @Transactional
//    public List<WorkSession> autoClockOutStaleSessions() {
//        LocalDateTime now = LocalDateTime.now(PK_ZONE);
//        LocalDateTime cutoff = now.minusHours(24); // 24 hours old sessions
//
//        return workSessionRepository.findByClockOutIsNullAndClockInBefore(cutoff);
//
//    }

    
//    @Transactional
//    public List<WorkSession> getLastSessionByEmployee(Long id) {
////    	System.out.println("runs repo query");
//    	return  workSessionRepository.findByEmployeeIdAndClockOutIsNull(id);
//
//    }
}
