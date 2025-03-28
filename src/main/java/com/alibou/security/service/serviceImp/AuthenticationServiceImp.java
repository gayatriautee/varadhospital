package com.alibou.security.service.serviceImp;

import com.alibou.security.config.AuthenticatedUserUtil;
import com.alibou.security.config.JwtService;
import com.alibou.security.dto.*;
import com.alibou.security.entity.Appointment;
import com.alibou.security.entity.Token;
import com.alibou.security.enums.Role;
import com.alibou.security.enums.TokenType;
import com.alibou.security.exception.ApiException;
import com.alibou.security.exception.UserNotFoundException;
import com.alibou.security.repository.AppointmentRepository;
import com.alibou.security.repository.TokenRepository;
import com.alibou.security.repository.UserRepository;
import com.alibou.security.service.AuthenticationService;
import com.alibou.security.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImp implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final AppointmentRepository appointmentRepository;
    private final AuthenticatedUserUtil authenticatedUserUtil;


    Logger logger = LoggerFactory.getLogger(AuthenticationServiceImp.class);
    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,"Email is already in use"); // Custom exception
        }
        var user = User.builder()
                .yourName(request.getYourName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                //.role(request.getRole())
                .mobileNo(request.getMobileNo())
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .message("Registration Successful")
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            // Handle invalid credentials
            throw new BadCredentialsException("Invalid email or password");
        }

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        AuthenticationResponse authenticationResponse= AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .message("Login Successful")
                .build();
        return authenticationResponse;
    }

    @Override
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUserName(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    @Override
    public AppointmentResponse appointment(AppointmentRequest request) {
        Appointment appointment=new Appointment();
        AppointmentResponse appointmentResponse=new AppointmentResponse();
        logger.info("inside appointment method");
        try{
            logger.info("inside try block");
            appointment.setYourName(request.getYourName());
            appointment.setEmail(request.getEmail());
            appointment.setMobileNo(request.getMobileNo());
            appointment.setDepartment(request.getDepartment());
            appointment.setAppointmentDate(request.getDateOfAppointment());
            appointment.setAppointmentTime(request.getTimeOfAppointment());
            appointmentRepository.save(appointment);
            appointmentResponse.setMessage("Appointment Booked Successfully!");
        }catch (Exception e){
            logger.error("error in appointment method ", e.getStackTrace());
        }

        return appointmentResponse;

    }

    @Override
    public List<AppointmentRequest> appointmentList(SortingRequest request) {
        int pageNo = request.getPageNo();
        int pageSize = request.getPageSize();
        String sortBy = request.getSortBy();
        String sortDir = request.getSortDir();
        //todo
        return List.of();
    }


    //saving token whenever user registers or logs inS
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

/*due to the above method i can create n number of tokens that are active which is not good
i want a maximum of one token to be active at a given instance
for that i wrote the below method
*/
    private void revokeAllUserTokens(User user){
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if(validUserTokens.isEmpty()){
            return;
        }
        validUserTokens.forEach(t->{
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

}
