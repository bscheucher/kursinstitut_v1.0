package com.bildungsinsitut.deutschkurse.model;

import com.bildungsinsitut.deutschkurse.enums.TrainerStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "trainer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainer_id")
    private Long id;

    @Column(name = "vorname", nullable = false, length = 100)
    private String vorname;

    @Column(name = "nachname", nullable = false, length = 100)
    private String nachname;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "telefon", length = 20)
    private String telefon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "abteilung_id")
    private Abteilung abteilung;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TrainerStatus status = TrainerStatus.VERFUEGBAR;

    @Column(name = "qualifikationen", columnDefinition = "TEXT")
    private String qualifikationen;

    @Column(name = "einstellungsdatum")
    private LocalDate einstellungsdatum;

    @Column(name = "aktiv")
    private Boolean aktiv = true;

    @Column(name = "erstellt_am", updatable = false)
    private LocalDateTime erstelltAm;

    @Column(name = "geaendert_am")
    private LocalDateTime geaendertAm;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL)
    private List<Kurs> kurse;

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