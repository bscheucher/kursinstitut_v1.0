package com.bildungsinsitut.deutschkurse.repository;

import com.bildungsinsitut.deutschkurse.enums.Role;
import com.bildungsinsitut.deutschkurse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<User> findByEnabledTrue();

    List<User> findByRole(Role role);

    // FIXED: Changed method name to match the parameter signature
    List<User> findByRoleAndEnabled(Role role, Boolean enabled);

    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :loginTime WHERE u.id = :userId")
    void updateLastLogin(Integer userId, LocalDateTime loginTime);

    @Modifying
    @Query("UPDATE User u SET u.enabled = :enabled WHERE u.id = :userId")
    void updateUserStatus(Integer userId, Boolean enabled);
}