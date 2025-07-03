package com.bildungsinsitut.deutschkurse.repository;

import com.bildungsinsitut.deutschkurse.model.Bewertung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BewertungRepository extends JpaRepository<Bewertung, Integer> {
    List<Bewertung> findByTeilnehmerId(Integer teilnehmerId);
    List<Bewertung> findByKursId(Integer kursId);
    List<Bewertung> findByTeilnehmerIdAndKursId(Integer teilnehmerId, Integer kursId);
    List<Bewertung> findByTestDatumBetween(LocalDate startDate, LocalDate endDate);
    List<Bewertung> findByBestandenFalse();
}