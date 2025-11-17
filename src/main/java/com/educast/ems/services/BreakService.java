package com.educast.ems.services;

import com.educast.ems.dto.BreakRequestDTO;
import com.educast.ems.dto.BreakResponseDTO;

import java.util.List;

public interface BreakService {

    BreakResponseDTO addBreak(BreakRequestDTO requestDTO);

    BreakResponseDTO updateBreak(Long id, BreakRequestDTO requestDTO);

    void deleteBreak(Long id);

    List<BreakResponseDTO> getBreaksBySession(Long sessionId);
    
    BreakResponseDTO getActiveBreak(Long sessionId); // ✅ Add this method

}
