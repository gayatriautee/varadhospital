package com.alibou.security.dto;

import com.alibou.security.enums.Role;
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
public class RegisterRequest {
    @NotEmpty(message = "Please fill the name field")
    private String yourName;
    @NotEmpty(message = "Please fill the email field")
    @Email
    private String email;
    @NotEmpty
    @Size(min = 8, message = "Password should have minimum of 8 characters")
    private String password;
    @NotEmpty
    @Size(min = 10, max = 10, message = "Mobile number must have 10 digits")
    private String mobileNo;
    private Role role;
}
