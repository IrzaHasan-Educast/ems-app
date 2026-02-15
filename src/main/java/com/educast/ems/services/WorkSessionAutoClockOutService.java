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
        
        // Yahan hum 2 hours minus kar rahe hain taakay 8 bje walay scheduler 
        // ka effect 6 bje (ya actual shift end) par khatam ho jaye.
//        LocalDateTime effectiveClockOutTime = now.minusHours(2);

        // End ongoing breaks
        List<Break> breaks = session.getBreaks();

        if (breaks != null) {
            breaks.stream()
                .filter(b -> b.getEndTime() == null) // Agar break open hai
                .forEach(b -> {
                    // Break ko bhi 2 ghantay pehlay band karein
                    // Taakay break ke hours bhi extra count na hon
                    
                    // Check: Agar break start hi 2 ghantay ke andar hui hai (edge case), 
                    // tw break ka start time hi end time ban jaye (0 duration), 
                    // otherwise effective time.
                    LocalDateTime breakEndTime = now.isAfter(b.getStartTime()) 
                                                 ? now 
                                                 : b.getStartTime();
                    
                    b.setEndTime(breakEndTime);
                    b.setDurationHours(
                        Duration.between(b.getStartTime(), breakEndTime).toSeconds() / 3600.0
                    );
                });
        }

        // Calculate total and net hours using now (not 'now')
        long totalSeconds = Duration.between(session.getClockIn(), now).getSeconds();
        
        // Negative duration se bachne ke liye (agar session abhi start hua ho ghalti se)
        if (totalSeconds < 0) totalSeconds = 0;

        long breakSeconds = session.getBreaks() == null ? 0 :
                session.getBreaks().stream()
                        .filter(b -> b.getStartTime() != null)
                        .mapToLong(b -> Duration.between(b.getStartTime(),
                                b.getEndTime() != null ? b.getEndTime() : now).getSeconds())
                        .sum();
        
//        System.out.println("Auto Clockout Executed using time: " + now);

        long netSeconds = Math.max(0, totalSeconds - breakSeconds);

        session.setTotalSessionHours(totalSeconds / 3600.0);
        session.setIdleHours(breakSeconds / 3600.0);
        session.setTotalHours(netSeconds / 3600.0);

        // Auto clock-out status with effective time
        session.setClockOut(now);
        session.setStatus("Auto Clocked Out");

        workSessionRepository.save(session);
    }

    // Fetch sessions in rolling window
    public List<WorkSession> getSessionsForAutoClockOut(LocalDateTime windowStart) {
        return workSessionRepository.findByClockOutIsNullAndClockInAfter(windowStart);
    }
}