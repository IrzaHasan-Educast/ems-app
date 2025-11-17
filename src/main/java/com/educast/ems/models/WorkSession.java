package com.educast.ems.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "work_sessions")
public class WorkSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDateTime clockIn;

    private LocalDateTime clockOut;

    @Column(nullable = true)
    private Double totalHours;  // Computed as clockOut - clockIn - breaks

    @Column(nullable = true)
    private Double idleHours;   // Optional, time spent idle

    @OneToMany(mappedBy = "workSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Break> breaks;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private String status;
}
