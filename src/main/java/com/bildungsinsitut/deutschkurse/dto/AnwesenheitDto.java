package com.bildungsinsitut.deutschkurse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AnwesenheitDto {
    private Integer id;

    @NotNull(message = "Teilnehmer ID is required")
    private Integer teilnehmerId;

    private String teilnehmerName; // Read-only, populated by mapper

    @NotNull(message = "Kurs ID is required")
    private Integer kursId;

    private String kursName; // Read-only, populated by mapper

    @NotNull(message = "Datum is required")
    private LocalDate datum;

    private Boolean anwesend = true;

    private Boolean entschuldigt = false;

    private String bemerkung;
}