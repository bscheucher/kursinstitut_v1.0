// TeilnehmerKursDto.java
package com.bildungsinsitut.deutschkurse.dto;

import com.bildungsinsitut.deutschkurse.enums.TeilnehmerKursStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TeilnehmerKursDto {
    private Integer id;
    private Integer teilnehmerId;
    private String teilnehmerName;
    private Integer kursId;
    private String kursName;
    private LocalDate anmeldedatum;
    private LocalDate abmeldedatum;
    private TeilnehmerKursStatus status;
    private BigDecimal abschlussnote;
    private String bemerkungen;
}