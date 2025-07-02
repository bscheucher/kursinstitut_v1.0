package com.bildungsinsitut.deutschkurse.controller;

import com.bildungsinsitut.deutschkurse.dto.KursDto;
import com.bildungsinsitut.deutschkurse.service.KursService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/kurse")
public class KursController {

    private final KursService kursService;

    public KursController(KursService kursService) {
        this.kursService = kursService;
    }

    @GetMapping
    public ResponseEntity<List<KursDto>> getAllKurse() {
        return ResponseEntity.ok(kursService.getAllKurse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<KursDto> getKursById(@PathVariable Long id) {
        return ResponseEntity.ok(kursService.getKursById(id));
    }

    // Add POST, PUT, DELETE endpoints
}