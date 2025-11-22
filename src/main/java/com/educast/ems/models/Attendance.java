package com.educast.ems.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Data
@Entity
public class Attendance {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;
	private boolean present;
	private LocalTime attendanceTime;
	private LocalDate attendanceDate;
	private Shift shift;
	
	
	@Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
	
	@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
	

}
