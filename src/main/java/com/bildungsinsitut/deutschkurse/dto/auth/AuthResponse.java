// AuthResponse.java
package com.bildungsinsitut.deutschkurse.dto.auth;

import com.bildungsinsitut.deutschkurse.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Integer userId;
    private String username;
    private String email;
    private String fullName;
    private Role role;

    public AuthResponse(String token, Integer userId, String username, String email, String fullName, Role role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }
}
