package com.bildungsinsitut.deutschkurse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KursraumDto {
    private Long id;
    private Long abteilungId;
    private String raumName;
    private Integer kapazitaet;
    private String ausstattung;
    private Boolean verfuegbar;
}