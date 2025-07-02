package com.bildungsinsitut.deutschkurse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KursraumDto {
    private Integer id;
    private Integer abteilungId;
    private String raumName;
    private Integer kapazitaet;
    private String ausstattung;
    private Boolean verfuegbar;
}