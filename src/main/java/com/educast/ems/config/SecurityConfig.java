package com.educast.ems.config;

import com.educast.ems.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // Support comma-separated origins
    @Value("${frontend.url}")
    private String frontendUrl;

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .authorizeHttpRequests(auth -> auth
                // CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public endpoints
                .requestMatchers("/api/v1/auth/**").permitAll()

                // Optional (uncomment if you use these)
                // .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // .requestMatchers("/actuator/health").permitAll()

                // Role protected routes
                .requestMatchers("/api/v1/users/me").hasAnyRole("ADMIN", "HR", "EMPLOYEE", "MANAGER")
                .requestMatchers("/api/v1/employees/manager/employees").hasRole("MANAGER")
                .requestMatchers("/api/v1/employees/my").hasAnyRole("ADMIN","HR","MANAGER","EMPLOYEE")
                .requestMatchers("/api/v1/employees/**").hasAnyRole("ADMIN", "HR")
                
                .requestMatchers("/api/v1/employee-shifts/shift/employee-count/**").hasRole("MANAGER")
                .requestMatchers("/api/v1/employee-shifts/employees/**").hasRole("MANAGER")
                .requestMatchers("/api/v1/employee-shifts/my").hasRole("EMPLOYEE")
                .requestMatchers("/api/v1/employee-shifts/**").hasAnyRole("ADMIN","HR")
                
                .requestMatchers("/api/v1/shifts/my/**").hasRole("MANAGER")
                .requestMatchers("/api/v1/shifts/**").hasAnyRole("ADMIN","HR")
                
                .requestMatchers("/api/v1/work-sessions/manager/**").hasRole("MANAGER")
                .requestMatchers("/api/v1/work-sessions/**").hasAnyRole("EMPLOYEE", "MANAGER")
                .requestMatchers("/api/v1/admin/work-sessions/**").hasAnyRole("ADMIN", "HR")

                .requestMatchers("/api/v1/attendance/my/**").hasAnyRole("EMPLOYEE", "MANAGER")
                .requestMatchers("/api/v1/attendance/all/**").hasAnyRole("ADMIN", "HR")
                .requestMatchers("/api/v1/attendance/mark/**").hasAnyRole("EMPLOYEE","MANAGER")
                .requestMatchers("/api/v1/attendance/manager/**").hasRole("MANAGER")
                .requestMatchers("/api/v1/attendance/absent/today").hasAnyRole("HR","ADMIN", "MANAGER")
//                .requestMatchers("/api/v1/attendance/absent/history").hasAnyRole("HR","ADMIN","MANAGER")
                
                

                .requestMatchers("/api/v1/leaves").hasAnyRole("EMPLOYEE", "MANAGER")
                .requestMatchers("/api/v1/leaves/admin").hasAnyRole("ADMIN", "HR")
                .requestMatchers("/api/v1/leaves/manager").hasRole("MANAGER")
                .requestMatchers("/api/v1/leaves/employee/**").hasAnyRole("EMPLOYEE", "MANAGER")
                .requestMatchers("/api/v1/leaves/types").hasAnyRole("EMPLOYEE", "ADMIN", "HR", "MANAGER")
                .requestMatchers("/api/v1/leaves/**").hasAnyRole("ADMIN", "HR")

                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow multiple origins separated by comma in properties
        List<String> origins = Arrays.stream(frontendUrl.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        configuration.setAllowedOrigins(origins);
//        configuration.setAllowedOrigins(List.of(frontendUrl));
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
