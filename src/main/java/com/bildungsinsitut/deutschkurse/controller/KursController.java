package com.bildungsinsitut.deutschkurse.controller;

import com.bildungsinsitut.deutschkurse.dto.KursDto;
import com.bildungsinsitut.deutschkurse.enums.KursStatusType;
import com.bildungsinsitut.deutschkurse.service.KursService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/public/kurse")
@CrossOrigin(origins = "*") // Configure this properly for production
public class KursController {

    private final KursService kursService;

    public KursController(KursService kursService) {
        this.kursService = kursService;
    }

    /**
     * Get all courses
     * GET /api/v1/kurse
     */
    @GetMapping
    public ResponseEntity<List<KursDto>> getAllKurse() {
        return ResponseEntity.ok(kursService.getAllKurse());
    }

    /**
     * Get course by ID
     * GET /api/v1/kurse/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<KursDto> getKursById(@PathVariable Integer id) {
        return ResponseEntity.ok(kursService.getKursById(id));
    }

    /**
     * Get courses by status
     * GET /api/v1/kurse/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<KursDto>> getKurseByStatus(@PathVariable KursStatusType status) {
        return ResponseEntity.ok(kursService.getKurseByStatus(status));
    }

    /**
     * Get current courses (planned and running)
     * GET /api/v1/kurse/aktuelle
     */
    @GetMapping("/aktuelle")
    public ResponseEntity<List<KursDto>> getAktuelleKurse() {
        return ResponseEntity.ok(kursService.getAktuelleKurse());
    }

    /**
     * Get available courses (with available spots)
     * GET /api/v1/kurse/verfuegbar
     */
    @GetMapping("/verfuegbar")
    public ResponseEntity<List<KursDto>> getVerfuegbareKurse() {
        return ResponseEntity.ok(kursService.getVerfuegbareKurse());
    }

    /**
     * Get courses by trainer
     * GET /api/v1/kurse/trainer/{trainerId}
     */
    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<KursDto>> getKurseByTrainer(@PathVariable Integer trainerId) {
        return ResponseEntity.ok(kursService.getKurseByTrainer(trainerId));
    }

    /**
     * Get courses starting within a date range
     * GET /api/v1/kurse/startdatum?von=2024-01-01&bis=2024-12-31
     */
    @GetMapping("/startdatum")
    public ResponseEntity<List<KursDto>> getKurseByStartdatumRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate von,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bis) {
        return ResponseEntity.ok(kursService.getKurseByStartdatumRange(von, bis));
    }

    /**
     * Create a new course
     * POST /api/v1/kurse
     */
    @PostMapping
    public ResponseEntity<KursDto> createKurs(@Valid @RequestBody KursDto kursDto) {
        KursDto created = kursService.createKurs(kursDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing course
     * PUT /api/v1/kurse/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<KursDto> updateKurs(@PathVariable Integer id,
                                              @Valid @RequestBody KursDto kursDto) {
        return ResponseEntity.ok(kursService.updateKurs(id, kursDto));
    }

    /**
     * Update only the status of a course
     * PATCH /api/v1/kurse/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<KursDto> updateKursStatus(@PathVariable Integer id,
                                                    @RequestParam KursStatusType status) {
        return ResponseEntity.ok(kursService.updateKursStatus(id, status));
    }

    /**
     * Add a participant to a course
     * POST /api/v1/kurse/{id}/teilnehmer/add
     */
    @PostMapping("/{id}/teilnehmer/add")
    public ResponseEntity<KursDto> addTeilnehmerToKurs(@PathVariable Integer id) {
        return ResponseEntity.ok(kursService.addTeilnehmerToKurs(id));
    }

    /**
     * Remove a participant from a course
     * POST /api/v1/kurse/{id}/teilnehmer/remove
     */
    @PostMapping("/{id}/teilnehmer/remove")
    public ResponseEntity<KursDto> removeTeilnehmerFromKurs(@PathVariable Integer id) {
        return ResponseEntity.ok(kursService.removeTeilnehmerFromKurs(id));
    }

    /**
     * Delete a course
     * DELETE /api/v1/kurse/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKurs(@PathVariable Integer id) {
        kursService.deleteKurs(id);
        return ResponseEntity.noContent().build();
    }
}