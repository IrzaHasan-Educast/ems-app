package com.educast.ems.config;

import com.educast.ems.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true) // ensures @PreAuthorize works
public class SecurityConfig {

	// ✅ frontend URLs from application.properties (comma-separated)
    @Value("${frontend.url}")
    private String frontendUrl;
    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable()) // disable CSRF for APIs
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
//                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/v1/users/me").hasAnyRole("ADMIN", "HR") // <-- add this line
                .requestMatchers("/api/v1/employees/**").hasAnyRole("ADMIN", "HR")
                .requestMatchers("/api/v1/work-sessions/**").hasRole("EMPLOYEE")
                .requestMatchers("/api/v1/admin/work-sessions/**").hasAnyRole("ADMIN", "HR")
                // Attendance
                .requestMatchers("/api/v1/attendance/my/**").hasRole("EMPLOYEE") // only employee
                .requestMatchers("/api/v1/attendance/all/**").hasAnyRole("ADMIN", "HR")   
                .requestMatchers("/api/v1/attendance/mark/**").hasRole("EMPLOYEE") // employee marking
                // Leaves
                .requestMatchers("/api/v1/leaves").hasRole("EMPLOYEE") // apply leave (POST)
                .requestMatchers("/api/v1/leaves/admin").hasAnyRole("ADMIN", "HR") // apply leave (POST)
                .requestMatchers("/api/v1/leaves/employee/**").hasRole("EMPLOYEE") // get own leaves
                .requestMatchers("/api/v1/leaves/types").hasAnyRole("EMPLOYEE", "ADMIN", "HR")
                // Admin + HR
                .requestMatchers("/api/v1/leaves/**").hasAnyRole("ADMIN", "HR")
                .anyRequest().authenticated()
                
            )
            .cors(cors -> cors.configurationSource(corsConfigurationSource())); // use CORS bean

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
     // ✅ convert comma-separated frontend URLs into list
        List<String> allowedOrigins = Arrays.asList(frontendUrl.split(","));
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
