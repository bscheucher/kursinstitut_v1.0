package com.bildungsinsitut.deutschkurse.enums;

public enum TeilnehmerKursStatus {
    angemeldet("Angemeldet"),
    aktiv("Aktiv"),
    abgeschlossen("Abgeschlossen"),
    abgebrochen("Abgebrochen");

    private final String displayName;

    TeilnehmerKursStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}