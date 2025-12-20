package com.educast.ems.services;

import com.educast.ems.dto.BreakRequestDTO;
import com.educast.ems.dto.BreakResponseDTO;
import com.educast.ems.models.Break;
import com.educast.ems.models.WorkSession;
import com.educast.ems.repositories.BreakRepository;
import com.educast.ems.repositories.WorkSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BreakServiceImpl implements BreakService {

    private final BreakRepository breakRepository;
    private final WorkSessionRepository workSessionRepository;

    @Override
    public BreakResponseDTO addBreak(BreakRequestDTO requestDTO) {
        WorkSession session = workSessionRepository.findById(requestDTO.getWorkSessionId())
                .orElseThrow(() -> new RuntimeException("Work session not found"));

        Break brk = new Break();
        brk.setWorkSession(session);
        brk.setStartTime(LocalDateTime.now(ZoneId.of("Asia/Karachi")));
        brk.setEndTime(null); // Not ended yet
        brk.setDurationHours(null); // Will be calculated on end

        Break saved = breakRepository.save(brk);
        return mapToDTO(saved);
    }
    
    @Override
    public BreakResponseDTO getActiveBreak(Long sessionId) {
        Break active = breakRepository
                .findFirstByWorkSessionIdAndEndTimeIsNull(sessionId)
                .orElse(null);

        return active != null ? mapToDTO(active) : null;
    }

    @Override
    public BreakResponseDTO updateBreak(Long id, BreakRequestDTO requestDTO) {
        Break brk = breakRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Break not found"));

        // End the break
        brk.setEndTime(LocalDateTime.now(ZoneId.of("Asia/Karachi")));
        if (brk.getStartTime() == null) {
            throw new RuntimeException("Break start time missing");
        }
        Duration duration = Duration.between(brk.getStartTime(), brk.getEndTime());
        brk.setDurationHours(duration.toSeconds() / 3600.0);

        Break saved = breakRepository.save(brk);
        return mapToDTO(saved);
    }

    @Override
    public void deleteBreak(Long id) {
        breakRepository.deleteById(id);
    }

    @Override
    public List<BreakResponseDTO> getBreaksBySession(Long sessionId) {
        List<Break> breaks = breakRepository.findByWorkSessionId(sessionId);
        return breaks.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private BreakResponseDTO mapToDTO(Break brk) {
        BreakResponseDTO dto = new BreakResponseDTO();
        dto.setId(brk.getId());
        dto.setStartTime(brk.getStartTime());
        dto.setEndTime(brk.getEndTime());
        if (brk.getStartTime() != null && brk.getEndTime() != null) {
            dto.setBreakDuration(Duration.between(brk.getStartTime(), brk.getEndTime()));
        } else {
            dto.setBreakDuration(null);
        }
        return dto;
    }
}
