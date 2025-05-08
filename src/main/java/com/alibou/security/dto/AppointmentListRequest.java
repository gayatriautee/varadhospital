package com.alibou.security.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AppointmentListRequest {
    private int pageNo;
    private int pageSize;
    private String sortBy;
    private String sortDir;
    private LocalDate date;
}
