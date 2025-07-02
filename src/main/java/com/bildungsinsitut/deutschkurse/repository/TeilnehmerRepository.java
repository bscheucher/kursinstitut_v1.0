package com.bildungsinsitut.deutschkurse.repository;

import com.bildungsinsitut.deutschkurse.model.Teilnehmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeilnehmerRepository extends JpaRepository<Teilnehmer, Integer> {
    List<Teilnehmer> findByAktivTrue();
    List<Teilnehmer> findByEmailContainingIgnoreCase(String email);
    List<Teilnehmer> findByVornameContainingIgnoreCaseOrNachnameContainingIgnoreCase(
            String vorname, String nachname);
}