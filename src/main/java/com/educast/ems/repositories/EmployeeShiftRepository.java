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
    
    Optional<List<EmployeeShift>> findAllByShiftId(Long shiftId);
    
//    list of employees by manager id
    @Query("""
    	    SELECT es
    	    FROM EmployeeShift es
    	    JOIN es.shift s
    	    JOIN s.manager m
    	    WHERE m.id = :managerId
    	""")
    	List<EmployeeShift> findEmployeesByManagerId(@Param("managerId") Long managerId);
    

//    get list of employees by shift id
    @Query("""
    	    SELECT es
    	    FROM EmployeeShift es
    	    JOIN es.shift s
    	    WHERE s.id = :shiftId
    	""")
    	List<EmployeeShift> findEmployeesBShiftId(@Param("shiftId") Long shiftId);
    
//    get the count of total numbers of employees
//    working
    @Query("""
    		SELECT COUNT(*)
    		FROM EmployeeShift es
    		join es.shift s
    		join s.manager m
    		where m.id = :managerId
    		""")
    int findEmployeesCountByShiftManagerId(@Param("managerId") Long managerId);
}
