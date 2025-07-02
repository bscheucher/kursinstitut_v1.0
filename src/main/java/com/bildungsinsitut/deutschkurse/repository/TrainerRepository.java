package com.bildungsinsitut.deutschkurse.repository;

import com.bildungsinsitut.deutschkurse.enums.TrainerStatus;
import com.bildungsinsitut.deutschkurse.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    List<Trainer> findByAktivTrue();
    List<Trainer> findByStatusAndAktivTrue(TrainerStatus status);
    List<Trainer> findByAbteilungIdAndAktivTrue(Long abteilungId);
}
