package com.bildungsinsitut.deutschkurse.repository;

import com.bildungsinsitut.deutschkurse.model.Anwesenheit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnwesenheitRepository extends JpaRepository<Anwesenheit, Integer> {
    List<Anwesenheit> findByTeilnehmerIdAndKursId(Integer teilnehmerId, Integer kursId);
    List<Anwesenheit> findByKursIdAndDatum(Integer kursId, LocalDate datum);
    List<Anwesenheit> findByDatumBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(a) FROM Anwesenheit a WHERE a.teilnehmer.id = ?1 AND a.kurs.id = ?2 AND a.anwesend = true")
    Long countAnwesenheitByTeilnehmerAndKurs(Integer teilnehmerId, Integer kursId);
}