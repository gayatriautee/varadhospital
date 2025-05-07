package com.alibou.security.controller;

import com.alibou.security.dto.*;
import com.alibou.security.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request){
       return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/appointment")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentResponse> appointment(@RequestBody AppointmentRequest request){
        return ResponseEntity.ok(authenticationService.appointment(request));
    }

    @PostMapping("/appointment-list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> appointmentList(@RequestBody AppointmentListRequest request){
        return ResponseEntity.ok(authenticationService.appointmentList(request));
    }


    @PostMapping("/appointment-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> appointmentStatus(@RequestBody AppointmentStatusRequest request){
        return ResponseEntity.ok(authenticationService.appointmentStatus(request));
    }

}
