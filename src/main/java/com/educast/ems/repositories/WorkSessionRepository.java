package com.educast.ems.repositories;

import com.educast.ems.models.WorkSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkSessionRepository extends JpaRepository<WorkSession, Long> {
    List<WorkSession> findByEmployeeIdAndClockInBetween(Long employeeId, LocalDate start, LocalDate end);
    List<WorkSession> findByEmployeeId(Long employeeId);
}

