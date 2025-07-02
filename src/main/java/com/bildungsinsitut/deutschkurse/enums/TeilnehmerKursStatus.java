package com.bildungsinsitut.deutschkurse.enums;

public enum TeilnehmerKursStatus {
    ANGEMELDET("Angemeldet"),
    AKTIV("Aktiv"),
    ABGESCHLOSSEN("Abgeschlossen"),
    ABGEBROCHEN("Abgebrochen");

    private final String displayName;

    TeilnehmerKursStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}