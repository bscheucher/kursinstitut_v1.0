package com.bildungsinsitut.deutschkurse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KurstypDto {
    private Integer id;
    private String kurstypCode;
    private String kurstypName;
    private String beschreibung;
    private Integer levelOrder;
    private Boolean aktiv;
}