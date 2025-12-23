package com.educast.ems.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.educast.ems.models.WorkSession;
import com.educast.ems.services.WorkSessionAutoClockOutService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AutoClockOutScheduler {

    private final WorkSessionAutoClockOutService autoClockOutService;
    private static final ZoneId PK_ZONE = ZoneId.of("Asia/Karachi");

   

    // Morning job: 6 AM, 10-hour range (8 PM previous day → 6 AM)
    @Scheduled(cron = "0 0 6 * * ?", zone = "Asia/Karachi")
    @Transactional
    public void autoClockOutMorning() {
        LocalDateTime now = LocalDateTime.now(PK_ZONE);
        LocalDateTime windowStart = now.minusHours(10);

        List<WorkSession> sessions = autoClockOutService.getSessionsForAutoClockOut(windowStart);
        sessions.forEach(autoClockOutService::autoClockOutSession);
    }

    // Evening job: 7 PM, 10-hour range (9 AM → 7 PM)
    @Scheduled(cron = "0 0 19 * * ?", zone = "Asia/Karachi")
    @Transactional
    public void autoClockOutEvening() {
        LocalDateTime now = LocalDateTime.now(PK_ZONE);
        LocalDateTime windowStart = now.minusHours(10);

        
        List<WorkSession> sessions = autoClockOutService.getSessionsForAutoClockOut(windowStart);
        sessions.forEach(autoClockOutService::autoClockOutSession);
    }
    
//    @Scheduled(cron = "0 45 11 * * ?", zone = "Asia/Karachi")
//    @Transactional
//    public void outClockOutNow() {
//    	System.out.println(1);
//    	List<WorkSession> sessions = autoClockOutService.autoClockOutStaleSessions();
//
//    	System.out.println(2);
//    	sessions.forEach(autoClockOutService::autoClockOutSession);
//    }
}

