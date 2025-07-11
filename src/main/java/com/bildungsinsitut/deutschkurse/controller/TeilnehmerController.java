package com.bildungsinsitut.deutschkurse.controller;

import com.bildungsinsitut.deutschkurse.dto.*;
import com.bildungsinsitut.deutschkurse.service.TeilnehmerKursService;
import com.bildungsinsitut.deutschkurse.service.TeilnehmerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teilnehmer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Configure this properly for production
public class TeilnehmerController {

    private final TeilnehmerService teilnehmerService;
    private final TeilnehmerKursService teilnehmerKursService;

    /**
     * Get all students
     * GET /api/v1/teilnehmer
     */
    @GetMapping
    public ResponseEntity<List<TeilnehmerDto>> getAllTeilnehmer() {
        return ResponseEntity.ok(teilnehmerService.getAllTeilnehmer());
    }

    /**
     * Get student by ID
     * GET /api/v1/teilnehmer/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeilnehmerDto> getTeilnehmerById(@PathVariable Integer id) {
        return ResponseEntity.ok(teilnehmerService.getTeilnehmerById(id));
    }

    /**
     * Create a new student
     * POST /api/v1/teilnehmer
     */
    @PostMapping
    public ResponseEntity<TeilnehmerDto> createTeilnehmer(@Valid @RequestBody TeilnehmerDto teilnehmerDto) {
        TeilnehmerDto created = teilnehmerService.createTeilnehmer(teilnehmerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing student
     * PUT /api/v1/teilnehmer/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TeilnehmerDto> updateTeilnehmer(@PathVariable Integer id,
                                                          @Valid @RequestBody TeilnehmerDto teilnehmerDto) {
        return ResponseEntity.ok(teilnehmerService.updateTeilnehmer(id, teilnehmerDto));
    }

    /**
     * Delete a student (soft delete - set aktiv = false)
     * DELETE /api/v1/teilnehmer/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeilnehmer(@PathVariable Integer id) {
        teilnehmerService.deleteTeilnehmer(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search students by name
     * GET /api/v1/teilnehmer/search?name=John
     */
    @GetMapping("/search")
    public ResponseEntity<List<TeilnehmerDto>> searchTeilnehmerByName(@RequestParam String name) {
        return ResponseEntity.ok(teilnehmerService.searchTeilnehmerByName(name));
    }

    // ========================= ENROLLMENT ENDPOINTS =========================

    /**
     * Get all courses for a specific student
     * GET /api/v1/teilnehmer/{teilnehmerId}/kurse
     */
    @GetMapping("/{teilnehmerId}/kurse")
    public ResponseEntity<List<KursDto>> getKurseForTeilnehmer(@PathVariable Integer teilnehmerId) {
        List<KursDto> kurse = teilnehmerKursService.getKurseForTeilnehmer(teilnehmerId);
        return ResponseEntity.ok(kurse);
    }

    /**
     * Enroll student in a course (alternative endpoint)
     * POST /api/v1/teilnehmer/{teilnehmerId}/kurse/{kursId}
     */
    @PostMapping("/{teilnehmerId}/kurse/{kursId}")
    public ResponseEntity<String> enrollInKurs(@PathVariable Integer teilnehmerId,
                                               @PathVariable Integer kursId) {
        teilnehmerKursService.enrollTeilnehmerInKurs(teilnehmerId, kursId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Student " + teilnehmerId + " successfully enrolled in course " + kursId);
    }

    /**
     * Remove student from a course (alternative endpoint)
     * DELETE /api/v1/teilnehmer/{teilnehmerId}/kurse/{kursId}
     */
    @DeleteMapping("/{teilnehmerId}/kurse/{kursId}")
    public ResponseEntity<String> removeFromKurs(@PathVariable Integer teilnehmerId,
                                                 @PathVariable Integer kursId) {
        teilnehmerKursService.removeTeilnehmerFromKurs(teilnehmerId, kursId);
        return ResponseEntity.ok("Student " + teilnehmerId + " successfully removed from course " + kursId);
    }
}