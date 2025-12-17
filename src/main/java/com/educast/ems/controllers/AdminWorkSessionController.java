package com.educast.ems.controllers;

import com.educast.ems.dto.WorkSessionResponseDTO;
import com.educast.ems.services.WorkSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/work-sessions")
@RequiredArgsConstructor
public class AdminWorkSessionController {

    private final WorkSessionService workSessionService;

    // ✅ Only ADMIN can access this endpoint
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public List<WorkSessionResponseDTO> getAllWorkSessions() {
        return workSessionService.getAllSessions();
    }
    
    @GetMapping("/all-optimized")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public List<WorkSessionResponseDTO> getAllWorkSessionsOptimized(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return workSessionService.getAllSessionsFiltered(employeeId, status, month, searchTerm, page, size);
    }

}
