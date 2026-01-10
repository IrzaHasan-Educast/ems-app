package com.educast.ems.repositories;

import com.educast.ems.models.Employee;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // You can add custom queries later, e.g., findByDepartment, findByRole
	boolean existsByEmail(String email);
	List<Employee> findByRole(String role);
	
	@Query("""
            SELECT e
            FROM Employee e
            JOIN EmployeeShift es ON es.employee.id = e.id
            JOIN es.shift s
            WHERE s.manager.id = :managerId
            ORDER BY e.fullName ASC
        """)
        List<Employee> findAllEmployeesForManager(Long managerId);

}
