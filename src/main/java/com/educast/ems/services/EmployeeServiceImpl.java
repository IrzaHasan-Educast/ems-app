package com.educast.ems.services;

import com.educast.ems.models.Employee;
import com.educast.ems.models.User;
import com.educast.ems.models.Role;
import com.educast.ems.repositories.EmployeeRepository;
import com.educast.ems.repositories.UserRepository;
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

    /**
     * Create employee + linked user account
     * @param employee Employee object
     * @param username username for user
     * @param password password for user
     * @return saved Employee
     */
    public Employee createEmployee(Employee employee, String username, String password) {
        // Validate password
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        // Check username uniqueness
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Save employee
        Employee savedEmployee = employeeRepository.save(employee);

        // Create linked user
        User user = new User();
        user.setEmployee(savedEmployee);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));

        // Map employee role to user role (you can customize)
        if ("ADMIN".equalsIgnoreCase(employee.getRole())) {
            user.setRole(Role.ADMIN);
        } else if ("HR".equalsIgnoreCase(employee.getRole())) {
            user.setRole(Role.HR);
        } else {
            user.setRole(Role.EMPLOYEE);
        }

        userRepository.save(user);

        return savedEmployee;
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
            existing.setDesignation(employee.getDesignation());
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
