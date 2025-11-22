package com.educast.ems.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.educast.ems.models.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>{
    List<Attendance> findByEmployeeId(Long employeeId);
    boolean existsByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate date);

}
