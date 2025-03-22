package com.alibou.security.dto;

import com.alibou.security.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String yourName;
    private String email;
    private String password;
    private String mobileNo;
    private Role role;
}
