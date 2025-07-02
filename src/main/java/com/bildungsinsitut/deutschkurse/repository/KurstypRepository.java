package com.bildungsinsitut.deutschkurse.repository;

import com.bildungsinsitut.deutschkurse.model.Kurstyp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KurstypRepository extends JpaRepository<Kurstyp, Long> {
    List<Kurstyp> findByAktivTrueOrderByLevelOrder();
    Kurstyp findByKurstypCode(String code);
}
