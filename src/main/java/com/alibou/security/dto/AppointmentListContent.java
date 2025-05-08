package com.alibou.security.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentListContent {
    private Integer id;
    private String yourName;
    private String email;
    private String mobileNo;
    private String department;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime appointmentDate;
    private Boolean status;
    private LocalDateTime createdAt;
}
