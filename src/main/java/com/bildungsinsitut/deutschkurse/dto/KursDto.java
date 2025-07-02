package com.bildungsinsitut.deutschkurse.dto;

import com.bildungsinsitut.deutschkurse.enums.KursStatusType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class KursDto {
    private Long id;
    private String kursName;
    private Long kurstypId;
    private String kurstypName;
    private Long kursraumId;
    private String kursraumName;
    private Long trainerId;
    private String trainerName;
    private LocalDate startdatum;
    private LocalDate enddatum;
    private Integer maxTeilnehmer;
    private Integer aktuelleTeilnehmer;
    private KursStatusType status;
    private String beschreibung;
}