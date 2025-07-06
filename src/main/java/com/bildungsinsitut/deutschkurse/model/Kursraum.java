package com.bildungsinsitut.deutschkurse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "kursraeume")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Kursraum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kursraum_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "abteilung_id", nullable = false)
    @JsonIgnore
    private Abteilung abteilung;

    @Column(name = "raum_name", nullable = false, length = 50)
    private String raumName;

    @Column(name = "kapazitaet")
    private Integer kapazitaet = 12;

    @Column(name = "ausstattung", columnDefinition = "TEXT")
    private String ausstattung;

    @Column(name = "verfuegbar")
    private Boolean verfuegbar = true;

    @Column(name = "erstellt_am", updatable = false)
    private LocalDateTime erstelltAm;

    @OneToMany(mappedBy = "kursraum", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Kurs> kurse;

    @PrePersist
    protected void onCreate() {
        erstelltAm = LocalDateTime.now();
    }
}