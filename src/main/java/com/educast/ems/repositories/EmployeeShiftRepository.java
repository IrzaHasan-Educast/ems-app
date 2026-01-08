package com.educast.ems.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.educast.ems.models.EmployeeShift;

import jakarta.transaction.Transactional;

public interface EmployeeShiftRepository extends JpaRepository<EmployeeShift, Long> {
    Optional<EmployeeShift> findByEmployeeId(Long employeeId);
    List<EmployeeShift> findByShiftId(Long id);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM EmployeeShift es WHERE es.shift.id = :shiftId")
    void deleteAllByShiftId(@Param("shiftId") Long shiftId);    

}
