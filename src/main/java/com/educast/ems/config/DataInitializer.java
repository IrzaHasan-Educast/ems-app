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
//
//            // ----- HR -----
//            if (!userRepo.existsByUsername("hr")) {
//
//                Employee hrEmp = new Employee(
//                        null,
//                        "HR User",
//                        "hr@educast.com",
//                        "03007654321",
//                        "Female",
//                        "Human Resources",
//                        "HR Manager",
//                        "HR",
//                        LocalDate.now(),
//                        true
//                );
//                employeeRepo.save(hrEmp);
//
//                User hrUser = new User(null, hrEmp, "hr", passwordEncoder.encode("hr123"), Role.HR);
//                userRepo.save(hrUser);
//
//                System.out.println("HR user created successfully!");
//            } else {
//                System.out.println("HR user already exists.");
//            }
//
//            // ----- ADMIN -----
//            if (!userRepo.existsByUsername("admin")) {
//
//                Employee adminEmp = new Employee(
//                        null,
//                        "Admin User",
//                        "admin@educast.com",
//                        "03001234567",
//                        "Male",
//                        "Management",
//                        "Administrator",
//                        "ADMIN",
//                        LocalDate.now(),
//                        true
//                );
//                employeeRepo.save(adminEmp);
//
//                User adminUser = new User(null, adminEmp, "admin", passwordEncoder.encode("admin123"), Role.ADMIN);
//                userRepo.save(adminUser);
//
//                System.out.println("Admin user created successfully!");
//            } else {
//                System.out.println("Admin user already exists.");
//            }
//
//            // ----- EMPLOYEE -----
//            if (!userRepo.existsByUsername("employee")) {
//
//                Employee employeeEmp = new Employee(
//                        null,
//                        "Employee User",
//                        "employee@educast.com",
//                        "03009876543",
//                        "Male",
//                        "Engineering",
//                        "Software Engineer",
//                        "EMPLOYEE",
//                        LocalDate.now(),
//                        true
//                );
//                employeeRepo.save(employeeEmp);
//
//                User employeeUser = new User(null, employeeEmp, "employee", passwordEncoder.encode("employee123"), Role.EMPLOYEE);
//                userRepo.save(employeeUser);
//
//                System.out.println("Employee user created successfully!");
//            } else {
//                System.out.println("Employee user already exists.");
//            }
//
//        };
//    }
//
//}
