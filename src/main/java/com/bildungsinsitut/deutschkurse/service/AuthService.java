package com.bildungsinsitut.deutschkurse.service;

import com.bildungsinsitut.deutschkurse.dto.auth.*;
import com.bildungsinsitut.deutschkurse.enums.Role;
import com.bildungsinsitut.deutschkurse.exception.ResourceNotFoundException;
import com.bildungsinsitut.deutschkurse.model.User;
import com.bildungsinsitut.deutschkurse.repository.UserRepository;
import com.bildungsinsitut.deutschkurse.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            log.info("Attempting login for user: {}", loginRequest.getUsername());

            // Try to authenticate - this will throw BadCredentialsException if invalid
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();

            // Update last login time
            userRepository.updateLastLogin(user.getId(), LocalDateTime.now());

            // Generate JWT token with additional claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("role", user.getRole().name());
            claims.put("email", user.getEmail());

            String token = jwtUtil.generateToken(user, claims);

            log.info("User {} logged in successfully", user.getUsername());

            return new AuthResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getRole()
            );

        } catch (AuthenticationException e) {
            // This catches both BadCredentialsException and other auth failures
            log.error("Login failed for user: {} - {}", loginRequest.getUsername(), e.getMessage());
            throw new BadCredentialsException("Invalid username or password");
        } catch (Exception e) {
            // Catch any other unexpected errors
            log.error("Unexpected error during login for user: {} - {}", loginRequest.getUsername(), e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred during login");
        }
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("Attempting to register user: {}", registerRequest.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setRole(registerRequest.getRole() != null ? registerRequest.getRole() : Role.USER);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        user = userRepository.save(user);

        // Generate JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        claims.put("email", user.getEmail());

        String token = jwtUtil.generateToken(user, claims);

        log.info("User {} registered successfully", user.getUsername());

        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
        );
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return convertToDto(user);
    }

    /**
     * Update current user's profile information
     */
    public UserDto updateCurrentUser(String currentUsername, UpdateUserRequest updateRequest) {
        log.info("Attempting to update user profile for: {}", currentUsername);

        // Get current user
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        // Validate username uniqueness if changed
        if (StringUtils.hasText(updateRequest.getUsername()) &&
                !updateRequest.getUsername().equals(user.getUsername())) {

            if (userRepository.existsByUsername(updateRequest.getUsername())) {
                throw new IllegalArgumentException("Username is already taken");
            }
            user.setUsername(updateRequest.getUsername());
            log.info("Username updated for user {}", user.getId());
        }

        // Validate email uniqueness if changed
        if (StringUtils.hasText(updateRequest.getEmail()) &&
                !updateRequest.getEmail().equals(user.getEmail())) {

            if (userRepository.existsByEmail(updateRequest.getEmail())) {
                throw new IllegalArgumentException("Email is already registered");
            }
            user.setEmail(updateRequest.getEmail());
            log.info("Email updated for user {}", user.getId());
        }

        // Update password if provided
        if (StringUtils.hasText(updateRequest.getPassword())) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            log.info("Password updated for user {}", user.getId());
        }

        // Update first name if provided
        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName().trim());
            log.info("First name updated for user {}", user.getId());
        }

        // Update last name if provided
        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName().trim());
            log.info("Last name updated for user {}", user.getId());
        }

        // Save updated user
        user = userRepository.save(user);
        log.info("User profile updated successfully for user: {}", user.getUsername());

        return convertToDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void updateUserStatus(Integer userId, Boolean enabled) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        userRepository.updateUserStatus(userId, enabled);
        log.info("User {} status updated to: {}", userId, enabled);
    }

    public UserDto updateUserRole(Integer userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setRole(newRole);
        user = userRepository.save(user);

        log.info("User {} role updated to: {}", user.getUsername(), newRole);
        return convertToDto(user);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        dto.setEnabled(user.getEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());
        return dto;
    }
}