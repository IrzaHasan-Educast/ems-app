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

   

    // Morning job: 8 AM, 12-hour range (8 PM previous day → 8 AM)
//    actual timing 10pm to 6am
    @Scheduled(cron = "0 0 8 * * ?", zone = "Asia/Karachi")
    @Transactional
    public void autoClockOutMorning() {
        LocalDateTime now = LocalDateTime.now(PK_ZONE);
        LocalDateTime windowStart = now.minusHours(12);

        List<WorkSession> sessions = autoClockOutService.getSessionsForAutoClockOut(windowStart);
        sessions.forEach(autoClockOutService::autoClockOutSession);
    }

    // Evening job: 8 PM, 10-hour range (8 AM → 8 PM)
//  actual timing 10am to 6pm
    @Scheduled(cron = "0 0 20 * * ?", zone = "Asia/Karachi")
    @Transactional
    public void autoClockOutEvening() {
        LocalDateTime now = LocalDateTime.now(PK_ZONE);
        LocalDateTime windowStart = now.minusHours(12);

        
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

