package com.bildungsinsitut.deutschkurse.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "anwesenheit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Anwesenheit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "anwesenheit_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teilnehmer_id", nullable = false)
    private Teilnehmer teilnehmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kurs_id", nullable = false)
    private Kurs kurs;

    @Column(name = "datum", nullable = false)
    private LocalDate datum;

    @Column(name = "anwesend")
    private Boolean anwesend = true;

    @Column(name = "entschuldigt")
    private Boolean entschuldigt = false;

    @Column(name = "bemerkung", columnDefinition = "TEXT")
    private String bemerkung;

    @Column(name = "erfasst_am", updatable = false)
    private LocalDateTime erfasstAm;

    @PrePersist
    protected void onCreate() {
        erfasstAm = LocalDateTime.now();
    }
}