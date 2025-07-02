package com.bildungsinsitut.deutschkurse.model;

import com.bildungsinsitut.deutschkurse.enums.TeilnehmerKursStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "teilnehmer_kurse")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeilnehmerKurs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zuordnung_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teilnehmer_id", nullable = false)
    private Teilnehmer teilnehmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kurs_id", nullable = false)
    private Kurs kurs;

    @Column(name = "anmeldedatum")
    private LocalDate anmeldedatum = LocalDate.now();

    @Column(name = "abmeldedatum")
    private LocalDate abmeldedatum;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TeilnehmerKursStatus status = TeilnehmerKursStatus.ANGEMELDET;

    @Column(name = "abschlussnote", precision = 3, scale = 2)
    private BigDecimal abschlussnote;

    @Column(name = "bemerkungen", columnDefinition = "TEXT")
    private String bemerkungen;

    @Column(name = "erstellt_am", updatable = false)
    private LocalDateTime erstelltAm;

    @Column(name = "geaendert_am")
    private LocalDateTime geaendertAm;

    @PrePersist
    protected void onCreate() {
        erstelltAm = LocalDateTime.now();
        geaendertAm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        geaendertAm = LocalDateTime.now();
    }
}
