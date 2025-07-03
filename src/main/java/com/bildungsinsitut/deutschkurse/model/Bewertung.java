package com.bildungsinsitut.deutschkurse.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bewertungen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bewertung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bewertung_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teilnehmer_id", nullable = false)
    private Teilnehmer teilnehmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kurs_id", nullable = false)
    private Kurs kurs;

    @Column(name = "test_typ", nullable = false, length = 20)
    private String testTyp;  // zwischentest, abschlusstest, muendlich, schriftlich

    @Column(name = "test_datum", nullable = false)
    private LocalDate testDatum;

    @Column(name = "punkte_erreicht", precision = 5, scale = 2)
    private BigDecimal punkteErreicht;

    @Column(name = "punkte_maximal", precision = 5, scale = 2)
    private BigDecimal punkteMaximal;

    @Column(name = "note", precision = 3, scale = 2)
    private BigDecimal note;

    @Column(name = "bestanden")
    private Boolean bestanden;

    @Column(name = "kommentar", columnDefinition = "TEXT")
    private String kommentar;

    @Column(name = "erstellt_am", updatable = false)
    private LocalDateTime erstelltAm;

    @PrePersist
    protected void onCreate() {
        erstelltAm = LocalDateTime.now();
    }
}