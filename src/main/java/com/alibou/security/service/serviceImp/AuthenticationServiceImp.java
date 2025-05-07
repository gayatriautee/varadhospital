package com.alibou.security.service.serviceImp;

import com.alibou.security.config.AuthenticatedUserUtil;
import com.alibou.security.config.JwtService;
import com.alibou.security.dto.*;
import com.alibou.security.email.EmailService;
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
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthenticationServiceImp implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final AppointmentRepository appointmentRepository;
    private ModelMapper mapper;
    private final EmailService emailService;


    Logger logger = LoggerFactory.getLogger(AuthenticationServiceImp.class);

    public AuthenticationServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, TokenRepository tokenRepository, AppointmentRepository appointmentRepository, ModelMapper mapper, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
        this.appointmentRepository = appointmentRepository;
        this.mapper = mapper;
        this.emailService = emailService;
    }

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
//        var jwtToken = jwtService.generateToken(user);
 //       saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken("")
                .message("User Added Successfully!")
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
        revokeAllUserTokens(user);
        cleanUpRevokedTokens();
        saveUserToken(user, jwtToken);
        AuthenticationResponse authenticationResponse= AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .message("Login Successful")
                .build();
        return authenticationResponse;
    }

    @Override
    @Transactional
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
            Appointment response = appointmentRepository.save(appointment);
            appointmentResponse.setMessage("Appointment Booked Successfully!");
            if(ObjectUtils.isNotEmpty(response)){
                LocalDateTime appointmentDate = response.getAppointmentDate(); // returns LocalDateTime
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");
                String formattedDate = appointmentDate.format(formatter);
                emailService.sendRegistrationEmail(response.getEmail(), response.getYourName(),
                        response.getMobileNo(), response.getDepartment(), formattedDate);
            }
        }catch (Exception e){
            logger.error("error in appointment method ", e.getStackTrace());
        }

        return appointmentResponse;

    }

    @Override
    public AppointmentListResponse appointmentList(AppointmentListRequest request) {
        AppointmentListResponse response = new AppointmentListResponse();
        int pageNo = request.getPageNo();
        int pageSize = request.getPageSize();
        String sortBy = request.getSortBy();
        String sortDir = request.getSortDir();

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Appointment> result = appointmentRepository.findAll(pageable);

        List<Appointment> appointments = result.getContent();

        List<AppointmentListContent> content= appointments.stream().map(appointment -> mapToDTO(appointment)).collect(Collectors.toList());

        response.setContent(content);
        response.setPageNo(result.getNumber());
        response.setPageSize(result.getSize());
        response.setTotalElements(result.getTotalElements());
        response.setTotalPages(result.getTotalPages());
        response.setIsLast(result.isLast());


        return response;
    }

    @Override
    public AppointmentResponse appointmentStatus(AppointmentStatusRequest request) {
        AppointmentResponse response = new AppointmentResponse();
        Appointment appointmentStatus = new Appointment();
        try{
            Optional<Appointment> appointment = appointmentRepository.findById(request.getId());
            if(appointment.isPresent()) appointmentStatus = appointment.get();
            appointmentStatus.setStatus(request.getStatus());
            appointmentRepository.save(appointmentStatus);
            response.setMessage("Status updated successfully!");
        }catch (Exception e){
            e.printStackTrace();
            logger.error("error in status update");
        }
        return response;
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

    @Transactional
    public void cleanUpRevokedTokens() {
        tokenRepository.deleteAllRevokedTokens();
    }

    private AppointmentListContent mapToDTO(Appointment appointment){
//        using mapper to reduce code
        AppointmentListContent appointmentListContent = mapper.map(appointment,AppointmentListContent.class);
        return appointmentListContent;
    }

}
