package com.bildungsinsitut.deutschkurse.enums;

public enum TrainerStatus {
    verfuegbar("Verfügbar"),
    im_einsatz("Im Einsatz"),
    abwesend("Abwesend");

    private final String displayName;

    TrainerStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}