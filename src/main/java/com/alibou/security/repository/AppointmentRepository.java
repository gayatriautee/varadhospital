package com.alibou.security.repository;

import com.alibou.security.dto.AppointmentListResponse;
import com.alibou.security.entity.Appointment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
//    @Query(value = "SELECT new com.alibou.security.dto.AppointmentListResponse() FROM Appointment a " +
//            "WHERE  ")
//  List<AppointmentListResponse> findAllAppointments(Pageable pageable);

}
