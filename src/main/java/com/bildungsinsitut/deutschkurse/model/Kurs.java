package com.bildungsinsitut.deutschkurse.model;

import com.bildungsinsitut.deutschkurse.enums.KursStatusType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kurse")
@Getter
@Setter
public class Kurs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kurs_id")
    private Long id;

    @Column(name = "kurs_name", nullable = false, length = 200)
    private String kursName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kurstyp_id", nullable = false)
    private Kurstyp kurstyp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kursraum_id", nullable = false)
    private Kursraum kursraum;

    @Column(name = "startdatum", nullable = false)
    private LocalDate startdatum;

    @Column(name = "enddatum")
    private LocalDate enddatum;

    @Column(name = "max_teilnehmer")
    private Integer maxTeilnehmer;

    @Column(name = "aktuelle_teilnehmer_anzahl")
    private Integer aktuelleTeilnehmerAnzahl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private KursStatusType status;

    @Column(name = "beschreibung", columnDefinition = "TEXT")
    private String beschreibung;

    @Column(name = "erstellt_am", updatable = false)
    private LocalDateTime erstelltAm;

    @Column(name = "geaendert_am")
    private LocalDateTime geaendertAm;

    // You can also add @OneToMany relationships here, for example, to TeilnehmerKursZuordnung
}