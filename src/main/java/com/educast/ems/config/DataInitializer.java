//package com.educast.ems.config;
//
//import com.educast.ems.models.Employee;
//import com.educast.ems.models.Role;
//import com.educast.ems.models.User;
//import com.educast.ems.repositories.EmployeeRepository;
//import com.educast.ems.repositories.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.time.LocalDate;
//
//@Configuration
//@RequiredArgsConstructor
//public class DataInitializer {
//
//    private final PasswordEncoder passwordEncoder;
//
//    @Bean
//    CommandLineRunner initData(EmployeeRepository employeeRepo, UserRepository userRepo) {
//        return args -> {
//            // Create Employees
//            Employee adminEmp = new Employee(null, "Admin User", "admin@educast.com", null, "Male", null, null, LocalDate.now(), true);
//            Employee hrEmp = new Employee(null, "HR User", "hr@educast.com", null, "Female", null, null, LocalDate.now(), true);
//            Employee employeeEmp = new Employee(null, "Employee User", "employee@educast.com", null, "Male", null, null, LocalDate.now(), true);
//
//            employeeRepo.save(adminEmp);
//            employeeRepo.save(hrEmp);
//            employeeRepo.save(employeeEmp);
//
//            // Create Users with bcrypt passwords
//            User adminUser = new User(null, adminEmp, "admin", passwordEncoder.encode("admin123"), Role.ADMIN);
//            User hrUser = new User(null, hrEmp, "hr", passwordEncoder.encode("hr123"), Role.HR);
//            User employeeUser = new User(null, employeeEmp, "employee", passwordEncoder.encode("employee123"), Role.EMPLOYEE);
//
//            userRepo.save(adminUser);
//            userRepo.save(hrUser);
//            userRepo.save(employeeUser);
//
//            System.out.println("Sample users created successfully!");
//        };
//    }
//}
