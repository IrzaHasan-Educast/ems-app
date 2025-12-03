package com.educast.ems.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.educast.ems.models.Leave;
import com.educast.ems.models.LeaveStatus;

public interface LeaveRepository extends JpaRepository<Leave, Long> {
	List<Leave> findByEmployeeId(Long employeeId);
    List<Leave> findByStatus(LeaveStatus status);
}
