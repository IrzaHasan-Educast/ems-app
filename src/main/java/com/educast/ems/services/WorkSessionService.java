package com.educast.ems.services;

import com.educast.ems.dto.WorkSessionRequestDTO;
import com.educast.ems.dto.WorkSessionResponseDTO;

import java.util.List;
import java.util.Optional;

public interface WorkSessionService {

    WorkSessionResponseDTO clockIn(WorkSessionRequestDTO requestDTO);

    WorkSessionResponseDTO clockOut(Long id);

    WorkSessionResponseDTO getSessionById(Long id);

    WorkSessionResponseDTO getOngoingSessionByEmployee(Long employeeId);
    public Optional<WorkSessionResponseDTO> getActiveSession(Long empId) ;
    public List<WorkSessionResponseDTO> getAllSessions();
    List<WorkSessionResponseDTO> getSessionsByEmployee(Long employeeId);
    public List<WorkSessionResponseDTO> getAllSessionsFiltered(
    Long employeeId, String status, Integer month, String searchTerm, int page, int size);
//    WorkSessionResponseDTO syncSessionHours(Long sessionId, WorkSessionHoursSyncDTO dto);
    public List<WorkSessionResponseDTO> getLatest3SessionsByEmployee(Long employeeId);
    List<WorkSessionResponseDTO> getAllSessionsForManager(Long managerId);

}
