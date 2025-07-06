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
@Table(name = "abteilungen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Abteilung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "abteilung_id")
    private Integer id;

    @Column(name = "abteilung_name", nullable = false, length = 100)
    private String abteilungName;

    @Column(name = "beschreibung", columnDefinition = "TEXT")
    private String beschreibung;

    @Column(name = "aktiv")
    private Boolean aktiv = true;

    @Column(name = "erstellt_am", updatable = false)
    private LocalDateTime erstelltAm;

    @OneToMany(mappedBy = "abteilung", cascade = CascadeType.ALL)
    @JsonIgnore  // ADD THIS - prevents circular reference
    private List<Kursraum> kursraeume;

    @OneToMany(mappedBy = "abteilung", cascade = CascadeType.ALL)
    @JsonIgnore  // ADD THIS - prevents circular reference
    private List<Trainer> trainer;

    @PrePersist
    protected void onCreate() {
        erstelltAm = LocalDateTime.now();
    }
}