package com.alibou.security.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(
        name = "appointment",
        indexes = {
                @Index(name = "idx_email", columnList = "email"),
                @Index(name = "idx_mobile_no", columnList = "mobile_no"),
                @Index(name = "idx_appointment_date", columnList = "appointment_date"),
                @Index(name = "idx_department", columnList = "department"),
                @Index(name = "idx_status", columnList = "status")
        }
)
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
