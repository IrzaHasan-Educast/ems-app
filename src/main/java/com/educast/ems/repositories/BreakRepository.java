package com.educast.ems.repositories;

import com.educast.ems.models.Break;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BreakRepository extends JpaRepository<Break, Long> {
    List<Break> findByWorkSessionId(Long workSessionId);
}
