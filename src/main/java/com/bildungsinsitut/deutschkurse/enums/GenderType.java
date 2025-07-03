package com.bildungsinsitut.deutschkurse.enums;

public enum GenderType {
    m("Männlich"),
    w("Weiblich"),
    d("Divers");

    private final String displayName;

    GenderType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}