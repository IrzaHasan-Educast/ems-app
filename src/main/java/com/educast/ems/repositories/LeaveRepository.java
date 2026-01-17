package com.educast.ems.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.educast.ems.models.Leave;
import com.educast.ems.models.LeaveStatus;

public interface LeaveRepository extends JpaRepository<Leave, Long> {
	List<Leave> findByEmployeeId(Long employeeId);
    List<Leave> findByStatus(LeaveStatus status);
    
    @Query("""
    		SELECT l
    		FROM Leave l
    		JOIN EmployeeShift es ON es.employee.id = l.employee.id
    		JOIN es.shift s
    		WHERE s.manager.id =:managerId
    		ORDER BY l.appliedOn DESC
    		""")
    List<Leave> findEmployeeLeaveOfManagerShift(Long managerId);
}
