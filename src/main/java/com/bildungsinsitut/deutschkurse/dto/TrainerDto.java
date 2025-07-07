package com.bildungsinsitut.deutschkurse.dto;

import com.bildungsinsitut.deutschkurse.enums.TrainerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainerDto {
    private Integer id;

    @NotBlank(message = "Vorname is required")
    private String vorname;

    @NotBlank(message = "Nachname is required")
    private String nachname;

    @Email(message = "Email should be valid")
    private String email;

    private String telefon;

    @NotNull(message = "Abteilung ID is required")
    private Integer abteilungId;

    // Read-only field populated by mapper
    private String abteilungName;

    private TrainerStatus status = TrainerStatus.verfuegbar;

    private String qualifikationen;

    private LocalDate einstellungsdatum;

    private Boolean aktiv = true;

    // Removed the redundant abteilung field to avoid confusion
}