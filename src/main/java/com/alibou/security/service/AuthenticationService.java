package com.alibou.security.service;

import com.alibou.security.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
    AppointmentResponse appointment(AppointmentRequest request);

    List<AppointmentRequest> appointmentList(SortingRequest request);
}
