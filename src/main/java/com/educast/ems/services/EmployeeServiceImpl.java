package com.educast.ems.services;

import com.educast.ems.models.Employee;
import com.educast.ems.models.Role;
import com.educast.ems.models.User;
import com.educast.ems.repositories.EmployeeRepository;
import com.educast.ems.repositories.UserRepository;
import com.educast.ems.dto.EmployeeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Employee createEmployee(Employee employee, String username, String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        Employee savedEmployee = employeeRepository.save(employee);

        User user = new User();
        user.setEmployee(savedEmployee);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));

        if ("ADMIN".equalsIgnoreCase(employee.getRole())) user.setRole(Role.ADMIN);
//        else if ("HR".equalsIgnoreCase(employee.getRole())) user.setRole(Role.HR);
        else user.setRole(Role.EMPLOYEE);

        userRepository.save(user);
        return savedEmployee;
    }

    @Override
    public Employee updateEmployee(Long id, EmployeeRequest request) {
        return employeeRepository.findById(id).map(existing -> {
            existing.setFullName(request.getFullName());
            existing.setEmail(request.getEmail());
            existing.setPhone(request.getPhone());
            existing.setGender(request.getGender());
            existing.setDepartment(request.getDepartment());
            existing.setRole(request.getRole());
            existing.setDesignation(request.getDesignation());
            existing.setJoiningDate(request.getJoiningDate());
            existing.setActive(request.isActive());
            return employeeRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Employee not found with id " + id));
    }

    @Override
    public void deleteEmployee(Long id) {
        // Delete the linked user first
        userRepository.findByEmployeeId(id).ifPresent(userRepository::delete);

        // Then delete employee
        employeeRepository.deleteById(id);
    }

    
    
}
