package com.educast.ems.repositories;

import com.educast.ems.dto.WorkSessionEmployeeTableDTO;
import com.educast.ems.dto.WorkSessionTableDTO;
import com.educast.ems.models.WorkSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkSessionRepository extends JpaRepository<WorkSession, Long> {

    Optional<WorkSession> findFirstByEmployeeIdAndClockOutIsNullOrderByClockInDesc(Long employeeId);
    List<WorkSession> findByEmployeeIdAndClockOutIsNull(Long employeeId);
    List<WorkSession> findByClockOutIsNullAndClockInBefore(LocalDateTime cutoff);

    @Query("""
    		SELECT new com.educast.ems.dto.WorkSessionEmployeeTableDTO(
    		    ws.id,
    		    ws.clockIn,
    		    ws.clockOut,
    		    ws.totalHours,
    		    ws.idleHours,
    		    ws.totalSessionHours,
    		    ws.status
    		)
    		FROM WorkSession ws
    		WHERE ws.employee.id = :employeeId
    		ORDER BY ws.clockIn DESC
    		""")
    		List<WorkSessionEmployeeTableDTO> findAllForEmployee(@Param("employeeId") Long employeeId);

    
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
    @Query("""
    		SELECT new com.educast.ems.dto.WorkSessionTableDTO(
    		    ws.id,
    		    e.id,
    		    e.fullName,
    		    e.role,
    		    ws.clockIn,
    		    ws.clockOut,
    		    ws.totalHours,
    		    ws.idleHours,
    		    ws.totalSessionHours,
    		    ws.status
    		)
    		FROM WorkSession ws
    		JOIN ws.employee e
    		ORDER BY ws.clockIn DESC
    		""")
    		List<WorkSessionTableDTO> findAllForAdmin();

    List<WorkSession> findByClockOutIsNullAndClockInAfter(LocalDateTime windowStart);
    List<WorkSession> findTop3ByEmployeeIdOrderByClockInDesc(Long employeeId);

    @Query("""
            SELECT DISTINCT ws
            FROM WorkSession ws
            JOIN FETCH ws.employee e
            LEFT JOIN FETCH ws.breaks b
            JOIN EmployeeShift es ON es.employee.id = e.id
            JOIN es.shift s
            WHERE s.manager.id = :managerId
            ORDER BY ws.clockIn DESC
        """)
        List<WorkSession> findAllForManager(@Param("managerId") Long managerId);
}
