package com.educast.ems.repositories;

import com.educast.ems.models.WorkSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkSessionRepository extends JpaRepository<WorkSession, Long> {

    Optional<WorkSession> findFirstByEmployeeIdAndClockOutIsNullOrderByClockInDesc(Long employeeId);
    Optional<WorkSession> findByEmployeeIdAndClockOutIsNull(Long employeeId);

    List<WorkSession> findByEmployeeIdOrderByClockInDesc(Long employeeId);
    @Query("SELECT ws FROM WorkSession ws " +
            "JOIN FETCH ws.employee e " +
            "LEFT JOIN FETCH ws.breaks b " +
            "WHERE (:employeeId IS NULL OR e.id = :employeeId) " +
            "AND (:status IS NULL OR ws.status = :status) " +
            "AND (:month IS NULL OR MONTH(ws.clockIn) = :month) " +
            "ORDER BY ws.clockIn DESC")
     List<WorkSession> findFiltered(
             @Param("employeeId") Long employeeId,
             @Param("status") String status,
             @Param("month") Integer month
     );
}
