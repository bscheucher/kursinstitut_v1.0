package com.bildungsinsitut.deutschkurse.dto;

import com.bildungsinsitut.deutschkurse.enums.KursStatusType;
import com.bildungsinsitut.deutschkurse.validation.groups.OnCreate;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class KursDto {

    private Integer id;

    @NotBlank(message = "Kurs name is required")
    @Size(max = 200, message = "Kurs name must not exceed 200 characters")
    private String kursName;

    @NotNull(message = "Kurstyp ID is required")
    @Positive(message = "Kurstyp ID must be positive")
    private Integer kurstypId;

    private String kurstypName; // Read-only, populated by mapper

    @NotNull(message = "Kursraum ID is required")
    @Positive(message = "Kursraum ID must be positive")
    private Integer kursraumId;

    private String kursraumName; // Read-only, populated by mapper

    @NotNull(message = "Trainer ID is required")
    @Positive(message = "Trainer ID must be positive")
    private Integer trainerId;

    private String trainerName; // Read-only, populated by mapper

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future", groups = OnCreate.class)
    private LocalDate startdatum;

    @Future(message = "End date must be in the future", groups = OnCreate.class)
    private LocalDate enddatum;

    @Positive(message = "Maximum participants must be positive")
    @Max(value = 50, message = "Maximum participants cannot exceed 50")
    private Integer maxTeilnehmer = 12;

    @Min(value = 0, message = "Current participants cannot be negative")
    private Integer aktuelleTeilnehmer = 0;

    private KursStatusType status = KursStatusType.geplant;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String beschreibung;

    // Custom validation method that can be called in service layer
    public boolean isValidDateRange() {
        if (startdatum != null && enddatum != null) {
            return enddatum.isAfter(startdatum);
        }
        return true; // If either date is null, let other validations handle it
    }

    public boolean isParticipantCountValid() {
        if (aktuelleTeilnehmer != null && maxTeilnehmer != null) {
            return aktuelleTeilnehmer <= maxTeilnehmer;
        }
        return true;
    }
}