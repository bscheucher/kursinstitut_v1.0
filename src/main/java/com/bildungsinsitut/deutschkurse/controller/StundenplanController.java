package com.bildungsinsitut.deutschkurse.controller;

import com.bildungsinsitut.deutschkurse.dto.StundenplanDto;
import com.bildungsinsitut.deutschkurse.service.StundenplanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stundenplan")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StundenplanController {

    private final StundenplanService stundenplanService;

    /**
     * Get all active schedules
     * GET /api/v1/stundenplan
     */
    @GetMapping
    public ResponseEntity<List<StundenplanDto>> getAllStundenplaene() {
        return ResponseEntity.ok(stundenplanService.getAllStundenplaene());
    }

    /**
     * Get schedule by ID
     * GET /api/v1/stundenplan/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<StundenplanDto> getStundenplanById(@PathVariable Integer id) {
        return ResponseEntity.ok(stundenplanService.getStundenplanById(id));
    }

    /**
     * Get schedules for a specific course
     * GET /api/v1/stundenplan/kurs/{kursId}
     */
    @GetMapping("/kurs/{kursId}")
    public ResponseEntity<List<StundenplanDto>> getStundenplaeneByKurs(@PathVariable Integer kursId) {
        return ResponseEntity.ok(stundenplanService.getStundenplaeneByKurs(kursId));
    }

    /**
     * Get schedules for a specific weekday
     * GET /api/v1/stundenplan/wochentag/{wochentag}
     */
    @GetMapping("/wochentag/{wochentag}")
    public ResponseEntity<List<StundenplanDto>> getStundenplaeneByWochentag(@PathVariable String wochentag) {
        return ResponseEntity.ok(stundenplanService.getStundenplaeneByWochentag(wochentag));
    }

    /**
     * Create a new schedule entry
     * POST /api/v1/stundenplan
     */
    @PostMapping
    public ResponseEntity<StundenplanDto> createStundenplan(@Valid @RequestBody StundenplanDto stundenplanDto) {
        StundenplanDto created = stundenplanService.createStundenplan(stundenplanDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing schedule entry
     * PUT /api/v1/stundenplan/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<StundenplanDto> updateStundenplan(@PathVariable Integer id,
                                                            @Valid @RequestBody StundenplanDto stundenplanDto) {
        return ResponseEntity.ok(stundenplanService.updateStundenplan(id, stundenplanDto));
    }

    /**
     * Delete a schedule entry (soft delete)
     * DELETE /api/v1/stundenplan/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStundenplan(@PathVariable Integer id) {
        stundenplanService.deleteStundenplan(id);
        return ResponseEntity.noContent().build();
    }
}