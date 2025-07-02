package com.bildungsinsitut.deutschkurse.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class KursDto {
    private Long id;
    private String kursName;
    private Long kurstypId;
    private Long kursraumId;
    private LocalDate startdatum;
    private LocalDate enddatum;
    private String status;
}