package com.educast.ems.repositories;

import com.educast.ems.models.Break;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BreakRepository extends JpaRepository<Break, Long> {
    List<Break> findByWorkSessionId(Long workSessionId);
    Optional<Break> findFirstByWorkSessionIdAndEndTimeIsNull(Long sessionId);
}
