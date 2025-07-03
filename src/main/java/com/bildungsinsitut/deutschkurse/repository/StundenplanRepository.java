package com.bildungsinsitut.deutschkurse.repository;

import com.bildungsinsitut.deutschkurse.model.Stundenplan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StundenplanRepository extends JpaRepository<Stundenplan, Integer> {
    List<Stundenplan> findByKursId(Integer kursId);
    List<Stundenplan> findByWochentag(String wochentag);
    List<Stundenplan> findByAktivTrue();
}