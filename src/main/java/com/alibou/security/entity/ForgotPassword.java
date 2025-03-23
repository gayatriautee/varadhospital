package com.alibou.security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ForgotPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "fp_id")
    private Integer fpId;
    @Column(nullable = false, name = "otp")
    private Integer otp;
    @Column(nullable = false, name = "expiration_time")
    private Date expirationTime;
    @OneToOne(cascade = CascadeType.ALL)
    private User user;

}
