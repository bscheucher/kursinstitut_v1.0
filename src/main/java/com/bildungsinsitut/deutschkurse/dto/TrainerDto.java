package com.bildungsinsitut.deutschkurse.dto;

import com.bildungsinsitut.deutschkurse.enums.TrainerStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerDto {
    private Integer id;
    private String vorname;
    private String nachname;
    private String email;
    private String telefon;
    private TrainerStatus status;
    private String qualifikationen;
    private Boolean aktiv;
}