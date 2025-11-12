package com.educast.ems.repositories;

import com.educast.ems.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // You can add custom queries later, e.g., findByDepartment, findByRole
}
