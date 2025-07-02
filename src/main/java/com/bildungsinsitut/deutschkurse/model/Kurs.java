package com.bildungsinsitut.deutschkurse.model;

import com.bildungsinsitut.deutschkurse.enums.KursStatusType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "kurse")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(name = "startdatum", nullable = false)
    private LocalDate startdatum;

    @Column(name = "enddatum")
    private LocalDate enddatum;

    @Column(name = "max_teilnehmer")
    private Integer maxTeilnehmer = 12;

    @Column(name = "aktuelle_teilnehmer")
    private Integer aktuelleTeilnehmer = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private KursStatusType status = KursStatusType.geplant;

    @Column(name = "beschreibung", columnDefinition = "TEXT")
    private String beschreibung;

    @Column(name = "erstellt_am", updatable = false)
    private LocalDateTime erstelltAm;

    @Column(name = "geaendert_am")
    private LocalDateTime geaendertAm;

    @OneToMany(mappedBy = "kurs", cascade = CascadeType.ALL)
    private List<TeilnehmerKurs> teilnehmerKurse;

    @OneToMany(mappedBy = "kurs", cascade = CascadeType.ALL)
    private List<Stundenplan> stundenplaene;

    @OneToMany(mappedBy = "kurs", cascade = CascadeType.ALL)
    private List<Anwesenheit> anwesenheiten;

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