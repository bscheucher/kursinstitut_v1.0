package com.bildungsinsitut.deutschkurse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class StundenplanDto {
    private Integer id;

    @NotNull(message = "Kurs ID is required")
    private Integer kursId;

    private String kursName; // Read-only, populated by mapper

    @NotBlank(message = "Wochentag is required")
    private String wochentag; // Montag, Dienstag, etc.

    @NotNull(message = "Startzeit is required")
    private LocalTime startzeit;

    @NotNull(message = "Endzeit is required")
    private LocalTime endzeit;

    private String bemerkungen;

    private Boolean aktiv = true;

    // Validation method
    public boolean isValidTimeRange() {
        if (startzeit != null && endzeit != null) {
            return endzeit.isAfter(startzeit);
        }
        return true;
    }
}