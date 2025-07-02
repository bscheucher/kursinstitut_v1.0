package com.bildungsinsitut.deutschkurse.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "kurstypen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Kurstyp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kurstyp_id")
    private Integer id;

    @Column(name = "kurstyp_code", nullable = false, unique = true, length = 20)
    private String kurstypCode;

    @Column(name = "kurstyp_name", nullable = false, length = 100)
    private String kurstypName;

    @Column(name = "beschreibung", columnDefinition = "TEXT")
    private String beschreibung;

    @Column(name = "level_order")
    private Integer levelOrder;

    @Column(name = "aktiv")
    private Boolean aktiv = true;

    @OneToMany(mappedBy = "kurstyp", cascade = CascadeType.ALL)
    private List<Kurs> kurse;
}