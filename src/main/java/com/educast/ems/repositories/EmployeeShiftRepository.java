package com.educast.ems.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.educast.ems.models.EmployeeShift;

public interface EmployeeShiftRepository extends JpaRepository<EmployeeShift, Long> {
    Optional<EmployeeShift> findByEmployeeId(Long employeeId);
    List<EmployeeShift> findByShiftId(Long id);
    

}
