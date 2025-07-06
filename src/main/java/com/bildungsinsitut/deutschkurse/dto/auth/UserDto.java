package com.bildungsinsitut.deutschkurse.dto.auth;

import com.bildungsinsitut.deutschkurse.enums.Role;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDto {
    private Integer id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private Role role;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}