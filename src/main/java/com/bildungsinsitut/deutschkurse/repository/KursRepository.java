package com.bildungsinsitut.deutschkurse.repository;

import com.bildungsinsitut.deutschkurse.model.Kurs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KursRepository extends JpaRepository<Kurs, Long> {
    // You can add custom query methods here
    List<Kurs> findByStatus(String status);
}