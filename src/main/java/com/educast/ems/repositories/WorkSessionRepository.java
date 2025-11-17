package com.educast.ems.repositories;

import com.educast.ems.models.WorkSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkSessionRepository extends JpaRepository<WorkSession, Long> {

    Optional<WorkSession> findFirstByEmployeeIdAndClockOutIsNullOrderByClockInDesc(Long employeeId);
    Optional<WorkSession> findByEmployeeIdAndClockOutIsNull(Long employeeId);

    List<WorkSession> findByEmployeeIdOrderByClockInDesc(Long employeeId);
}
