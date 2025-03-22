package com.alibou.security.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Data
public class AppointmentRequest {
    private Date dateOfAppointment;
    private String department;
    private Time timeOfAppointment;
}
