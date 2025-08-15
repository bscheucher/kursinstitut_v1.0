package com.bildungsinsitut.deutschkurse.service;

import com.bildungsinsitut.deutschkurse.dto.auth.UpdateUserRequest;
import com.bildungsinsitut.deutschkurse.dto.auth.UserDto;
import com.bildungsinsitut.deutschkurse.enums.Role;
import com.bildungsinsitut.deutschkurse.exception.ResourceNotFoundException;
import com.bildungsinsitut.deutschkurse.model.User;
import com.bildungsinsitut.deutschkurse.repository.UserRepository;
import com.bildungsinsitut.deutschkurse.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        updateRequest = createUpdateRequest();
    }

    @Test
    void shouldUpdateUserProfile() {
        // Given
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = authService.updateCurrentUser(username, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("newusername");
        assertThat(result.getEmail()).isEqualTo("newemail@example.com");
        assertThat(result.getFirstName()).isEqualTo("NewFirstName");
        assertThat(result.getLastName()).isEqualTo("NewLastName");

        verify(userRepository).findByUsername(username);
        verify(userRepository).existsByUsername("newusername");
        verify(userRepository).existsByEmail("newemail@example.com");
        verify(passwordEncoder).encode("newpassword");
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Given
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("newusername")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.updateCurrentUser(username, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username is already taken");

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.updateCurrentUser(username, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email is already registered");

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.updateCurrentUser(username, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found: nonexistent");

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldUpdateOnlyProvidedFields() {
        // Given
        String username = "testuser";
        UpdateUserRequest partialUpdate = new UpdateUserRequest();
        partialUpdate.setFirstName("OnlyFirstName");
        // Other fields remain null

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = authService.updateCurrentUser(username, partialUpdate);

        // Then
        assertThat(result).isNotNull();

        // Verify that only firstName was updated
        verify(userRepository).save(argThat(user ->
                user.getFirstName().equals("OnlyFirstName") &&
                        user.getUsername().equals("testuser") && // Original username preserved
                        user.getEmail().equals("test@example.com") // Original email preserved
        ));
    }

    @Test
    void shouldNotUpdateUsernameIfSameAsOriginal() {
        // Given
        String username = "testuser";
        UpdateUserRequest sameUsernameUpdate = new UpdateUserRequest();
        sameUsernameUpdate.setUsername("testuser"); // Same as original
        sameUsernameUpdate.setFirstName("NewFirstName");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        authService.updateCurrentUser(username, sameUsernameUpdate);

        // Then
        verify(userRepository, never()).existsByUsername(anyString()); // Should not check uniqueness
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldNotUpdateEmailIfSameAsOriginal() {
        // Given
        String username = "testuser";
        UpdateUserRequest sameEmailUpdate = new UpdateUserRequest();
        sameEmailUpdate.setEmail("test@example.com"); // Same as original
        sameEmailUpdate.setFirstName("NewFirstName");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        authService.updateCurrentUser(username, sameEmailUpdate);

        // Then
        verify(userRepository, never()).existsByEmail(anyString()); // Should not check uniqueness
        verify(userRepository).save(any(User.class));
    }

    // Helper methods
    private User createTestUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private UpdateUserRequest createUpdateRequest() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("newusername");
        request.setEmail("newemail@example.com");
        request.setPassword("newpassword");
        request.setFirstName("NewFirstName");
        request.setLastName("NewLastName");
        return request;
    }
}