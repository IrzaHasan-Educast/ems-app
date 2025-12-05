package com.educast.ems.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "leaves")
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false)
    private LeaveType leaveType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "duration", nullable = false)
    private int  duration;

    @Column(name = "description")
    private String description;

    @Column(name = "prescription_img")
    private String prescriptionImg; // store file path or URL

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LeaveStatus status = LeaveStatus.PENDING;

    @Column(name = "applied_on", nullable = false)
    private LocalDateTime appliedOn = LocalDateTime.now();

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    // Getters and Setters

}
