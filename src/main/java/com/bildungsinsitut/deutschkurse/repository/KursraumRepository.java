package com.bildungsinsitut.deutschkurse.repository;

import com.bildungsinsitut.deutschkurse.model.Kursraum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KursraumRepository extends JpaRepository<Kursraum, Integer> {
    List<Kursraum> findByVerfuegbarTrue();
    List<Kursraum> findByAbteilungId(Integer abteilungId);
}