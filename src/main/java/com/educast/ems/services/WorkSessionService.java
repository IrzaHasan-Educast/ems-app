package com.educast.ems.services;

import com.educast.ems.dto.WorkSessionRequestDTO;
import com.educast.ems.dto.WorkSessionResponseDTO;

import java.util.List;

public interface WorkSessionService {

    WorkSessionResponseDTO clockIn(WorkSessionRequestDTO requestDTO);

    WorkSessionResponseDTO clockOut(Long id);

    WorkSessionResponseDTO getSessionById(Long id);

    List<WorkSessionResponseDTO> getSessionsByEmployee(Long employeeId);
}
