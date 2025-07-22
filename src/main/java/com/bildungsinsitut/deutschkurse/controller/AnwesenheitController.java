package com.bildungsinsitut.deutschkurse.controller;

import com.bildungsinsitut.deutschkurse.dto.AnwesenheitDto;
import com.bildungsinsitut.deutschkurse.dto.BulkAnwesenheitDto;
import com.bildungsinsitut.deutschkurse.service.AnwesenheitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/anwesenheit")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnwesenheitController {

    private final AnwesenheitService anwesenheitService;

    /**
     * Get all attendance records
     * GET /api/v1/anwesenheit
     */
    @GetMapping
    public ResponseEntity<List<AnwesenheitDto>> getAllAnwesenheiten() {
        return ResponseEntity.ok(anwesenheitService.getAllAnwesenheiten());
    }

    /**
     * Get attendance by ID
     * GET /api/v1/anwesenheit/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnwesenheitDto> getAnwesenheitById(@PathVariable Integer id) {
        return ResponseEntity.ok(anwesenheitService.getAnwesenheitById(id));
    }

    /**
     * Get attendance for a specific course on a specific date
     * GET /api/v1/anwesenheit/kurs/{kursId}/datum/{datum}
     */
    @GetMapping("/kurs/{kursId}/datum/{datum}")
    public ResponseEntity<List<AnwesenheitDto>> getAnwesenheitByKursAndDatum(
            @PathVariable Integer kursId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datum) {
        return ResponseEntity.ok(anwesenheitService.getAnwesenheitByKursAndDatum(kursId, datum));
    }

    /**
     * Get attendance history for a specific student in a specific course
     * GET /api/v1/anwesenheit/teilnehmer/{teilnehmerId}/kurs/{kursId}
     */
    @GetMapping("/teilnehmer/{teilnehmerId}/kurs/{kursId}")
    public ResponseEntity<List<AnwesenheitDto>> getAnwesenheitByTeilnehmerAndKurs(
            @PathVariable Integer teilnehmerId,
            @PathVariable Integer kursId) {
        return ResponseEntity.ok(anwesenheitService.getAnwesenheitByTeilnehmerAndKurs(teilnehmerId, kursId));
    }

    /**
     * Get attendance statistics for a student in a course
     * GET /api/v1/anwesenheit/statistik/teilnehmer/{teilnehmerId}/kurs/{kursId}
     */
    @GetMapping("/statistik/teilnehmer/{teilnehmerId}/kurs/{kursId}")
    public ResponseEntity<Map<String, Object>> getAnwesenheitStatistik(
            @PathVariable Integer teilnehmerId,
            @PathVariable Integer kursId) {
        return ResponseEntity.ok(anwesenheitService.getAnwesenheitStatistik(teilnehmerId, kursId));
    }

    /**
     * Get attendance for date range
     * GET /api/v1/anwesenheit/zeitraum?startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/zeitraum")
    public ResponseEntity<List<AnwesenheitDto>> getAnwesenheitByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(anwesenheitService.getAnwesenheitByDateRange(startDate, endDate));
    }

    /**
     * Create or update attendance for a single student
     * POST /api/v1/anwesenheit
     */
    @PostMapping
    public ResponseEntity<AnwesenheitDto> createOrUpdateAnwesenheit(@Valid @RequestBody AnwesenheitDto anwesenheitDto) {
        AnwesenheitDto result = anwesenheitService.createOrUpdateAnwesenheit(anwesenheitDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Bulk create/update attendance for multiple students
     * POST /api/v1/anwesenheit/bulk
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<AnwesenheitDto>> createBulkAnwesenheit(@Valid @RequestBody BulkAnwesenheitDto bulkDto) {
        List<AnwesenheitDto> results = anwesenheitService.createBulkAnwesenheit(bulkDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(results);
    }

    /**
     * Delete attendance record
     * DELETE /api/v1/anwesenheit/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnwesenheit(@PathVariable Integer id) {
        anwesenheitService.deleteAnwesenheit(id);
        return ResponseEntity.noContent().build();
    }
}