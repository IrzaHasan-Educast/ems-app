package com.educast.ems.services;

import com.educast.ems.models.Employee;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    List<Employee> getAllEmployees();

    Optional<Employee> getEmployeeById(Long id);

//    Employee createEmployee(Employee employee);

    Employee updateEmployee(Long id, Employee employee);

    void deleteEmployee(Long id);

	/**
	 * Create employee and linked user account
	 */
	Employee createEmployee(Employee employee, String username, String password);
    
}
