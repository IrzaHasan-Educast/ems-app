package com.educast.ems.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.educast.ems.models.Attendance;
import com.educast.ems.models.Employee;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>{
    List<Attendance> findByEmployeeId(Long employeeId);
    boolean existsByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate date);
    
 // Custom query to get absent employees on a specific date
    @Query("SELECT e FROM Employee e WHERE e.active = true AND e.id NOT IN " +
           "(SELECT a.employee.id FROM Attendance a WHERE a.attendanceDate = :date)")
    List<Employee> findActiveEmployeesAbsentOn(LocalDate date);

    // Get all absent employees between joining and leaving (or current) date
    @Query("SELECT e FROM Employee e WHERE e.id NOT IN " +
           "(SELECT a.employee.id FROM Attendance a WHERE a.attendanceDate BETWEEN e.joiningDate AND :endDate)")
    List<Employee> findEmployeesAbsentBetween(LocalDate endDate);

}
