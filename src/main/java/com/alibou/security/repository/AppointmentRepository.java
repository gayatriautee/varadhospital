package com.alibou.security.repository;

import com.alibou.security.dto.AppointmentListContent;
import com.alibou.security.dto.AppointmentListResponse;
import com.alibou.security.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
//    @Query(value = "SELECT new com.alibou.security.dto.AppointmentListResponse() FROM Appointment a " +
//            "WHERE  ")
//  List<AppointmentListResponse> findAllAppointments(Pageable pageable);


    @Query("SELECT new com.alibou.security.dto.AppointmentListContent(a.id, a.yourName, a.email, a.mobileNo, a.department, a.appointmentDate, a.status, a.createdAt ) FROM Appointment a " +
            "WHERE a.appointmentDate BETWEEN :start AND :end")
    Page<AppointmentListContent> findAllByAppointmentDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

}
