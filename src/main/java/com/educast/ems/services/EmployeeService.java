package com.educast.ems.services;

import com.educast.ems.models.Employee;
import com.educast.ems.dto.EmployeeRequest;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Optional<Employee> getEmployeeById(Long id);
    Employee createEmployee(Employee employee, String username, String password);
    Employee updateEmployee(Long id, EmployeeRequest request);
    void deleteEmployee(Long id);
    Employee toggleActive(Long id);

}
