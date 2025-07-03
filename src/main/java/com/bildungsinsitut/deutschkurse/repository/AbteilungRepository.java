package com.bildungsinsitut.deutschkurse.repository;

import com.bildungsinsitut.deutschkurse.model.Abteilung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbteilungRepository extends JpaRepository<Abteilung, Integer> {
    List<Abteilung> findByAktivTrue();
    Abteilung findByAbteilungName(String abteilungName);
}