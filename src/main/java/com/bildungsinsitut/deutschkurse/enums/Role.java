package com.bildungsinsitut.deutschkurse.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN("Admin"),
    TRAINER("Trainer"),
    STAFF("Staff"),
    USER("User");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}