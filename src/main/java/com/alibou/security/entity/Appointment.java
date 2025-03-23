package com.alibou.security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Date;

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
    private Date appointmentDate;
    @Column(name = "department")
    private String department;
    @Column(name = "appointnment_time")
    private Time appointmentTime;
    @Column(name = "status")
    private Boolean status = true;
}
