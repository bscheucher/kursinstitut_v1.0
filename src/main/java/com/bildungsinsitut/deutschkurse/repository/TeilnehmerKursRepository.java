package com.bildungsinsitut.deutschkurse.repository;

import com.bildungsinsitut.deutschkurse.model.TeilnehmerKurs;
import com.bildungsinsitut.deutschkurse.enums.TeilnehmerKursStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeilnehmerKursRepository extends JpaRepository<TeilnehmerKurs, Integer> {
    List<TeilnehmerKurs> findByTeilnehmerId(Integer teilnehmerId);
    List<TeilnehmerKurs> findByKursId(Integer kursId);
    Optional<TeilnehmerKurs> findByTeilnehmerIdAndKursId(Integer teilnehmerId, Integer kursId);
    List<TeilnehmerKurs> findByStatus(TeilnehmerKursStatus status);
}