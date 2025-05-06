package com.alibou.security.dto;

import lombok.Data;

@Data
public class AppointmentListRequest {
    private int pageNo;
    private int pageSize;
    private String sortBy;
    private String sortDir;
}
