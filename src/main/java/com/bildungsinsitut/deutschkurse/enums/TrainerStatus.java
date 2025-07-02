package com.bildungsinsitut.deutschkurse.enums;

public enum TrainerStatus {
    VERFUEGBAR("Verfügbar"),
    IM_EINSATZ("Im Einsatz"),
    ABWESEND("Abwesend");

    private final String displayName;

    TrainerStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}