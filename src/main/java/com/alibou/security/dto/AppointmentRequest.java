package com.alibou.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentRequest {
    private Integer id;
    @NotEmpty(message = "Please fill the name field")
    private String yourName;
    @NotEmpty(message = "Please fill the email field")
    @Email
    private String email;
    @NotEmpty
    @Size(min = 10, max = 10, message = "Mobile number must have 10 digits")
    private String mobileNo;
    @NotEmpty(message = "Please select a department")
    private String department;
    @NotEmpty(message = "Please select a date for your appointment")
    private Date dateOfAppointment;
    @NotEmpty(message = "Please select the time of your appointment")
    private Time timeOfAppointment;
}
