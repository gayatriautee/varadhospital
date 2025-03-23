package com.alibou.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotEmpty(message = "Please fill the email field")
    @Email
    private String email;
    @Size(min = 8, message = "Password should have minimum of 8 characters")
    private String password;
}
