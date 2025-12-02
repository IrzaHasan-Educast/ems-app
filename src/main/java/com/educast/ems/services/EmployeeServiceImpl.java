package com.educast.ems.services;

import com.educast.ems.models.Employee;
import com.educast.ems.models.Role;
import com.educast.ems.models.User;
import com.educast.ems.repositories.EmployeeRepository;
import com.educast.ems.repositories.UserRepository;
import com.educast.ems.dto.EmployeeRequest;
import com.educast.ems.dto.EmployeeResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<EmployeeResponse> getAllEmployees() {
    	List<Employee> empList = employeeRepository.findAll();
    	return empList.stream()
        .map(this::mapToDto)   // use the private method
        .collect(Collectors.toList());
    }

    @Override
    public Optional<EmployeeResponse> getEmployeeById(Long id) {
    	return employeeRepository.findById(id)
                .map(this::mapToDto);
    }

    @Override
    public EmployeeResponse createEmployee(Employee employee, String username, String password) {
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
        else if ("HR".equalsIgnoreCase(employee.getRole())) user.setRole(Role.HR);
        else user.setRole(Role.EMPLOYEE);

        userRepository.save(user);
        EmployeeResponse savedEmpResponse = mapToDto(savedEmployee);
        return savedEmpResponse;
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee emp =  employeeRepository.findById(id).map(existing -> {
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
        
        return mapToDto(emp);
    }

    @Override
    public void deleteEmployee(Long id) {
        // Delete the linked user first
        userRepository.findByEmployeeId(id).ifPresent(userRepository::delete);

        // Then delete employee
        employeeRepository.deleteById(id);
    }

    @Override
    public EmployeeResponse toggleActive(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setActive(!employee.isActive());
        Employee emp = employeeRepository.save(employee);
        return mapToDto(emp);
    }
    
    
    private EmployeeResponse mapToDto(Employee emp) {
        if (emp == null) return null;

        EmployeeResponse dto = new EmployeeResponse();
        dto.setId(emp.getId());
        dto.setFullName(emp.getFullName());
        dto.setEmail(emp.getEmail());
        dto.setPhone(emp.getPhone());
        dto.setGender(emp.getGender());
        dto.setDepartment(emp.getDepartment());
        dto.setDesignation(emp.getDesignation());
        dto.setRole(emp.getRole());
        dto.setJoiningDate(emp.getJoiningDate());
        dto.setActive(emp.isActive());

        // If you have a linked user, you can set username here
        // dto.setUsername(emp.getUser() != null ? emp.getUser().getUsername() : null);

        return dto;
    }
}
