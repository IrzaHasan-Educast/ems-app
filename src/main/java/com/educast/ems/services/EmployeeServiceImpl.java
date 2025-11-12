package com.educast.ems.services;

import com.educast.ems.models.Employee;
import com.educast.ems.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Long id, Employee employee) {
        return employeeRepository.findById(id).map(existing -> {
            existing.setFullName(employee.getFullName());
            existing.setEmail(employee.getEmail());
            existing.setPhone(employee.getPhone());
            existing.setGender(employee.getGender());
            existing.setDepartment(employee.getDepartment());
            existing.setRole(employee.getRole());
            existing.setJoiningDate(employee.getJoiningDate());
            existing.setActive(employee.isActive());
            return employeeRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Employee not found with id " + id));
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}
