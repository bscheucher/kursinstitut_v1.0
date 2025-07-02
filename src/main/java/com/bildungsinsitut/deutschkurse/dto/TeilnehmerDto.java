package com.bildungsinsitut.deutschkurse.dto;

import com.bildungsinsitut.deutschkurse.enums.GenderType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class TeilnehmerDto {
    private Long id;
    private String vorname;
    private String nachname;
    private String email;
    private String telefon;
    private LocalDate geburtsdatum;
    private GenderType geschlecht;
    private String staatsangehoerigkeit;
    private String muttersprache;
    private LocalDate anmeldedatum;
    private Boolean aktiv;
}