package com.bildungsinsitut.deutschkurse.model;

import com.bildungsinsitut.deutschkurse.enums.GenderType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "teilnehmer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Teilnehmer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teilnehmer_id")
    private Integer id;

    @Column(name = "vorname", nullable = false, length = 100)
    private String vorname;

    @Column(name = "nachname", nullable = false, length = 100)
    private String nachname;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "telefon", length = 20)
    private String telefon;

    @Column(name = "geburtsdatum")
    private LocalDate geburtsdatum;

    @Enumerated(EnumType.STRING)
    @Column(name = "geschlecht", length = 1)  // Added length constraint to match DB CHAR(1)
    private GenderType geschlecht;

    @Column(name = "staatsangehoerigkeit", length = 100)
    private String staatsangehoerigkeit;

    @Column(name = "muttersprache", length = 100)
    private String muttersprache;

    @Column(name = "anmeldedatum")
    private LocalDate anmeldedatum = LocalDate.now();

    @Column(name = "aktiv")
    private Boolean aktiv = true;

    @Column(name = "erstellt_am", updatable = false)
    private LocalDateTime erstelltAm;

    @Column(name = "geaendert_am")
    private LocalDateTime geaendertAm;

    @OneToMany(mappedBy = "teilnehmer", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TeilnehmerKurs> teilnehmerKurse;

    @OneToMany(mappedBy = "teilnehmer", cascade = CascadeType.ALL)
    @JsonIgnore
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