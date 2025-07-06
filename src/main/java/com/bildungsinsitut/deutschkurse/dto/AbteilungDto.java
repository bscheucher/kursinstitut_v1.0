package com.bildungsinsitut.deutschkurse.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AbteilungDto {
    private Integer id;
    private String abteilungName;
    private String beschreibung;
    private Boolean aktiv;
}