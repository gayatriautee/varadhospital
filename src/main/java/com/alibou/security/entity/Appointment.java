package com.alibou.security.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String yourName;
    @Column(name = "email")
    private String email;
    @Column(name = "mobile_no")
    private String mobileNo;
    @Column(name = "appointment_date")
    private LocalDateTime appointmentDate;
    @Column(name = "department")
    private String department;
    @Column(name = "status")
    private Boolean status = true;
    @Column(name="created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name="updated_at")
    @CreationTimestamp
    private LocalDateTime updatedAt;
}
