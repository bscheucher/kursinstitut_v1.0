package com.bildungsinsitut.deutschkurse.dto;

import com.bildungsinsitut.deutschkurse.enums.KursStatusType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class KursDto {
    private Integer id;
    private String kursName;
    private Integer kurstypId;
    private String kurstypName;
    private Integer kursraumId;
    private String kursraumName;
    private Integer trainerId;
    private String trainerName;
    private LocalDate startdatum;
    private LocalDate enddatum;
    private Integer maxTeilnehmer;
    private Integer aktuelleTeilnehmer;
    private KursStatusType status;
    private String beschreibung;
}