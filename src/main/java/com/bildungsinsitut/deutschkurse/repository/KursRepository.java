package com.bildungsinsitut.deutschkurse.repository;

import com.bildungsinsitut.deutschkurse.enums.KursStatusType;
import com.bildungsinsitut.deutschkurse.model.Kurs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface KursRepository extends JpaRepository<Kurs, Long> {
    List<Kurs> findByStatus(KursStatusType status);
    List<Kurs> findByStatusIn(List<KursStatusType> statuses);
    List<Kurs> findByTrainerId(Long trainerId);
    List<Kurs> findByStartdatumBetween(LocalDate start, LocalDate end);

    @Query("SELECT k FROM Kurs k WHERE k.aktuelleTeilnehmer < k.maxTeilnehmer AND k.status = 'geplant'")
    List<Kurs> findVerfuegbareKurse();
}