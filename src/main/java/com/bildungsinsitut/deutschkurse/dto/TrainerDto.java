package com.bildungsinsitut.deutschkurse.dto;

import com.bildungsinsitut.deutschkurse.enums.TrainerStatus;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class TrainerDto {
    private Long id;
    private String vorname;
    private String nachname;
    private String email;
    private String telefon;
    private Long abteilungId;
    private String abteilungName;
    private TrainerStatus status;
    private String qualifikationen;
    private LocalDate einstellungsdatum;
    private Boolean aktiv;
}