package com.bildungsinsitut.deutschkurse.model;

import com.bildungsinsitut.deutschkurse.enums.TrainerStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "trainer")
@Getter
@Setter
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainer_id")
    private Integer id; // Make sure this is Integer, not Long

    @Column(name = "vorname", nullable = false, length = 100)
    private String vorname;

    @Column(name = "nachname", nullable = false, length = 100)
    private String nachname;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "telefon", length = 20)
    private String telefon;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TrainerStatus status = TrainerStatus.VERFUEGBAR;

    @Column(name = "qualifikationen", columnDefinition = "TEXT")
    private String qualifikationen;

    @Column(name = "aktiv")
    private Boolean aktiv = true;

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