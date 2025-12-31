package com.educast.ems.services;

import com.educast.ems.models.Employee;
import com.educast.ems.dto.EmployeeRequest;
import com.educast.ems.dto.EmployeeResByRoleDTO;
import com.educast.ems.dto.EmployeeResponse;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<EmployeeResponse> getAllEmployees();
    Optional<EmployeeResponse> getEmployeeById(Long id);
    EmployeeResponse createEmployee(Employee employee, String username, String password);
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);
    void deleteEmployee(Long id);
    EmployeeResponse toggleActive(Long id);
	List<EmployeeResByRoleDTO> findByRole(String role);

}
