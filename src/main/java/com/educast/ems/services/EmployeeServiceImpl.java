package com.educast.ems.services;

import com.educast.ems.models.Employee;
import com.educast.ems.models.Role;
import com.educast.ems.models.User;
import com.educast.ems.repositories.EmployeeRepository;
import com.educast.ems.repositories.UserRepository;
import com.educast.ems.dto.EmployeeRequest;
import com.educast.ems.dto.EmployeeResByRoleDTO;
import com.educast.ems.dto.EmployeeResponse;
import com.educast.ems.dto.WorkSessionResponseDTO;
import com.educast.ems.exception.DuplicateResourceException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WorkSessionService workSession;
    private final EmployeeShiftService employeeShiftService;

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
	@Transactional
	public EmployeeResponse createEmployee(EmployeeRequest dto) {
		Employee employee = new Employee();
        employee.setFullName(dto.getFullName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setGender(dto.getGender());
        employee.setDepartment(dto.getDepartment());
        employee.setDesignation(dto.getDesignation());
        employee.setRole(dto.getRole());
        employee.setJoiningDate(dto.getJoiningDate());
        employee.setActive(dto.isActive());
	
	    if (dto.getPassword() == null || dto.getPassword().length() < 6) {
	        throw new IllegalArgumentException("Password must be at least 6 characters");
	    }
	
	    // ✅ username check
	    if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
	        throw new DuplicateResourceException("Username already exists");
	    }
	
	    // ✅ email check (THIS WAS MISSING)
	    if (employeeRepository.existsByEmail(employee.getEmail())) {
	        throw new DuplicateResourceException("Email already exists");
	    }
	
	    Employee savedEmployee = employeeRepository.save(employee);
	    
	    if ("EMPLOYEE".equalsIgnoreCase(savedEmployee.getRole()) && dto.getShiftId() != null) {
	        employeeShiftService.assignedShift(savedEmployee.getId(), dto.getShiftId());
	    }
	    User user = new User();
	    user.setEmployee(savedEmployee);
	    user.setUsername(dto.getUsername());
	    user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
	
	    switch (employee.getRole().toUpperCase()) {
	        case "ADMIN" -> user.setRole(Role.ADMIN);
	        case "HR" -> user.setRole(Role.HR);
	        case "MANAGER" -> user.setRole(Role.MANAGER);
	        default -> user.setRole(Role.EMPLOYEE);
	    }
	
	    userRepository.save(user);
	
	    return mapToDto(savedEmployee);
	}
	
	public List<EmployeeResByRoleDTO> findByRole(String role){
		List<Employee> emp = employeeRepository.findByRole(role);
		return emp.stream().map(this:: mapToEmpRoleDto).toList();
	}


    @Override
	public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
	    String currentUserRole = getCurrentUserRole(); // logged-in user role
	
	    if ("ADMIN".equalsIgnoreCase(request.getRole()) && "HR".equalsIgnoreCase(currentUserRole)) {
	        throw new RuntimeException("HR cannot update Admin employee!");
	    }
	
	    Employee emp = employeeRepository.findById(id).map(existing -> {
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
        boolean isSessionActive =checkOngoingActivities(id);
        if(isSessionActive) {
        	System.out.println("Session was active");
        }else {
        	System.out.println("Not Activi");
        }
        employee.setActive(!employee.isActive());
        Employee emp = employeeRepository.save(employee);
        return mapToDto(emp);
    }
    
    private boolean checkOngoingActivities(Long empId) {
    	WorkSessionResponseDTO wResponse = workSession.getOngoingSessionByEmployee(empId);
    	if(wResponse != null) {
    		workSession.clockOut(wResponse.getId());
    		return true;
    	}
    	return false;
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
    
    private EmployeeResByRoleDTO mapToEmpRoleDto(Employee emp) {
    	EmployeeResByRoleDTO dto = new EmployeeResByRoleDTO();
    	dto.setId(emp.getId());
    	dto.setFullName(emp.getFullName());
    	dto.setRole(emp.getRole());
    	return dto;
    }
    
    private String getCurrentUserRole() {
        // If using Spring Security with JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().stream()
                    .findFirst()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .orElse("");
        }
        return "";
    }
}
