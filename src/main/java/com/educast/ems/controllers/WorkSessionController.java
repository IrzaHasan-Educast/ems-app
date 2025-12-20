package com.educast.ems.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.educast.ems.dto.WorkSessionRequestDTO;
import com.educast.ems.dto.WorkSessionResponseDTO;
import com.educast.ems.security.CustomUserDetails;
import com.educast.ems.services.WorkSessionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/work-sessions")
@RequiredArgsConstructor
public class WorkSessionController {

     private final WorkSessionService workSessionService; 

     @PostMapping("/clock-in")
     public ResponseEntity<WorkSessionResponseDTO> clockIn(
             @AuthenticationPrincipal CustomUserDetails userDetails) {

         Long employeeId = userDetails.getEmployeeId();

         WorkSessionResponseDTO session = workSessionService.clockIn(new WorkSessionRequestDTO(
                 employeeId,null, null
         ));

         return ResponseEntity.ok(session);
     }


     // Clock Out
     @PutMapping("/clock-out/{id}")
     public ResponseEntity<WorkSessionResponseDTO> clockOut(@PathVariable Long id) {
         WorkSessionResponseDTO session = workSessionService.clockOut(id);
         return ResponseEntity.ok(session);
     }

     // Get Current Session (for page reload)
     @GetMapping("/current")
     public ResponseEntity<WorkSessionResponseDTO> getCurrentSession(
             @AuthenticationPrincipal CustomUserDetails userDetails) {

         Long employeeId = userDetails.getEmployeeId();
         WorkSessionResponseDTO session = workSessionService.getOngoingSessionByEmployee(employeeId);
         if (session == null) return ResponseEntity.noContent().build();
         return ResponseEntity.ok(session);
     }

     @GetMapping("/employee/{employeeId}")
     public ResponseEntity<List<WorkSessionResponseDTO>> getByEmployee(@PathVariable Long employeeId) {
         List<WorkSessionResponseDTO> sessions = workSessionService.getSessionsByEmployee(employeeId);
         if (sessions.isEmpty()) {
             return ResponseEntity.noContent().build();
         }
         return ResponseEntity.ok(sessions);
     }


    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
         WorkSessionResponseDTO responseDTO = workSessionService.getSessionById(id);
         return ResponseEntity.ok(responseDTO);
//        return ResponseEntity.ok("Service not implemented yet"); // Temporary
    }
    
    @GetMapping("/active")
    public ResponseEntity<WorkSessionResponseDTO> getActiveSession(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long empId = userDetails.getEmployeeId();

        Optional<WorkSessionResponseDTO> session = workSessionService.getActiveSession(empId);

        return ResponseEntity.ok(session.orElse(null));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("employeeId", userDetails.getEmployeeId());
        resp.put("fullName", userDetails.getFullName());
        resp.put("role", userDetails.getRole());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/employee/{id}/latest-sessions")
    public List<WorkSessionResponseDTO> getLatestSessions(@PathVariable Long id) {
        return workSessionService.getLatest3SessionsByEmployee(id);
    }



}
