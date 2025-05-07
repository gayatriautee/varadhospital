package com.alibou.security.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Time;
@Data
public class AppointmentListContent {
    private Integer id;
    private String yourName;
    private String email;
    private String mobileNo;
    private String department;
    private Date appointmentDate;
    private Time appointmentTime;
    private Boolean status;
}
