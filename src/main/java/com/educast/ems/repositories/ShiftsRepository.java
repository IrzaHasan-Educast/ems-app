package com.educast.ems.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.educast.ems.models.Shifts;

public interface ShiftsRepository extends JpaRepository<Shifts, Long> {
	
	Shifts findByManagerId(Long managerId);

}
