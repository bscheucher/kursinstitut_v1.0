package com.bildungsinsitut.deutschkurse.repository;

import com.bildungsinsitut.deutschkurse.enums.TrainerStatus;
import com.bildungsinsitut.deutschkurse.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Integer > {
    List<Trainer> findByAktivTrue();
    List<Trainer> findByStatusAndAktivTrue(TrainerStatus status);
    List<Trainer> findByAbteilungIdAndAktivTrue(Integer abteilungId);


}
