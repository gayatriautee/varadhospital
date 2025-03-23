package com.alibou.security.controller;

import com.alibou.security.dto.VerifyEmailDto;
import com.alibou.security.entity.ForgotPassword;
import com.alibou.security.entity.User;
import com.alibou.security.records.ChangePassword;
import com.alibou.security.records.MailBody;
import com.alibou.security.repository.ForgotPasswordRepository;
import com.alibou.security.repository.UserRepository;
import com.alibou.security.service.EmailService;
import com.alibou.security.service.serviceImp.EmailServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/v1/password")
public class ForgotPasswordController {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    //send mail for email verification
    @PostMapping("/verifyMail")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestBody VerifyEmailDto request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("please provide a valid email"));
        ForgotPassword forgotPassword = forgotPasswordRepository.findByUser(user);

        if(ObjectUtils.isNotEmpty(forgotPassword)){
            disableForeignKeyChecks();
            forgotPasswordRepository.deleteByFpId(forgotPassword.getFpId());
        }
        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(request.getEmail())
                .text(String.format("This is the OTP for your forgot password request : %s. This is an automated email. Please do not reply to this email.\n" , otp))
                .subject("OTP for Forgot Password request")
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 70 * 1000))
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);
        Map<String, String> result = new HashMap<>();
        result.put("message","Email sent for verification successfully!");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("please provide a valid email"));
        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(()->new RuntimeException("Invalid otp for email : " + email));
        if(fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteByFpId(fp.getFpId());
            return new  ResponseEntity<>("OTP has expired", HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok("OTP has been verified");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword, @PathVariable String email){

        if(!Objects.equals(changePassword.password(), changePassword.repeatPassword())){
            return new  ResponseEntity<>("please enter the password again!", HttpStatus.EXPECTATION_FAILED);
        }
        String encodePassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encodePassword);
        return ResponseEntity.ok("Password changed successfully");
    }

    private Integer otpGenerator(){
        Random random = new Random();
        return  random.nextInt(100_000,999_999);
    }

    public void disableForeignKeyChecks() {
        jdbcTemplate.execute("SET foreign_key_checks = 0;");
    }

}
