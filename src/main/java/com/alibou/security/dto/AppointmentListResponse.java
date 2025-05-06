package com.alibou.security.dto;

import com.alibou.security.entity.Appointment;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Data
public class AppointmentListResponse {
  private List<AppointmentListContent> content;
  private Integer pageNo;
  private Integer pageSize;
  private Long totalElements;
  private Integer totalPages;
  private Boolean isLast;
}
