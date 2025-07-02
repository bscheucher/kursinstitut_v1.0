package com.bildungsinsitut.deutschkurse.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "stundenplan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stundenplan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stundenplan_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kurs_id", nullable = false)
    private Kurs kurs;

    @Column(name = "wochentag", nullable = false, length = 10)
    private String wochentag;

    @Column(name = "startzeit", nullable = false)
    private LocalTime startzeit;

    @Column(name = "endzeit", nullable = false)
    private LocalTime endzeit;

    @Column(name = "bemerkungen", columnDefinition = "TEXT")
    private String bemerkungen;

    @Column(name = "aktiv")
    private Boolean aktiv = true;

    @Column(name = "erstellt_am", updatable = false)
    private LocalDateTime erstelltAm;

    @PrePersist
    protected void onCreate() {
        erstelltAm = LocalDateTime.now();
    }
}
