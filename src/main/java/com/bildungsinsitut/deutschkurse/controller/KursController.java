package com.bildungsinsitut.deutschkurse.controller;

import com.bildungsinsitut.deutschkurse.dto.*;
import com.bildungsinsitut.deutschkurse.enums.KursStatusType;
import com.bildungsinsitut.deutschkurse.enums.TeilnehmerKursStatus;
import com.bildungsinsitut.deutschkurse.model.TeilnehmerKurs;
import com.bildungsinsitut.deutschkurse.service.KursService;
import com.bildungsinsitut.deutschkurse.service.TeilnehmerKursService;
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
@RequestMapping("/api/v1/kurse")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Configure this properly for production
public class KursController {

    private final KursService kursService;
    private final TeilnehmerKursService teilnehmerKursService;

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
     * Delete a course
     * DELETE /api/v1/kurse/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKurs(@PathVariable Integer id) {
        kursService.deleteKurs(id);
        return ResponseEntity.noContent().build();
    }

    // ========================= ENROLLMENT ENDPOINTS =========================

    /**
     * Get all students enrolled in a specific course
     * GET /api/v1/kurse/{kursId}/teilnehmer
     */
    @GetMapping("/{kursId}/teilnehmer")
    public ResponseEntity<List<TeilnehmerDto>> getTeilnehmerInKurs(@PathVariable Integer kursId) {
        List<TeilnehmerDto> teilnehmer = teilnehmerKursService.getTeilnehmerInKurs(kursId);
        return ResponseEntity.ok(teilnehmer);
    }

    /**
     * Enroll a student in a course
     * POST /api/v1/kurse/enroll
     */
    @PostMapping("/enroll")
    public ResponseEntity<Map<String, Object>> enrollTeilnehmer(@Valid @RequestBody EnrollmentRequest request) {
        TeilnehmerKurs enrollment = teilnehmerKursService.enrollTeilnehmerInKurs(
                request.getTeilnehmerId(), request.getKursId());

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Student successfully enrolled in course",
                "enrollmentId", enrollment.getId(),
                "teilnehmerId", request.getTeilnehmerId(),
                "kursId", request.getKursId(),
                "anmeldedatum", enrollment.getAnmeldedatum(),
                "status", enrollment.getStatus()
        ));
    }

    /**
     * Remove a student from a course
     * DELETE /api/v1/kurse/{kursId}/teilnehmer/{teilnehmerId}
     */
    @DeleteMapping("/{kursId}/teilnehmer/{teilnehmerId}")
    public ResponseEntity<Map<String, String>> removeTeilnehmerFromKurs(
            @PathVariable Integer kursId,
            @PathVariable Integer teilnehmerId) {

        teilnehmerKursService.removeTeilnehmerFromKurs(teilnehmerId, kursId);

        return ResponseEntity.ok(Map.of(
                "message", "Student successfully removed from course",
                "teilnehmerId", teilnehmerId.toString(),
                "kursId", kursId.toString()
        ));
    }

    /**
     * Update enrollment status
     * PATCH /api/v1/kurse/{kursId}/teilnehmer/{teilnehmerId}/status
     */
    @PatchMapping("/{kursId}/teilnehmer/{teilnehmerId}/status")
    public ResponseEntity<Map<String, Object>> updateEnrollmentStatus(
            @PathVariable Integer kursId,
            @PathVariable Integer teilnehmerId,
            @Valid @RequestBody StatusUpdateRequest request) {

        TeilnehmerKurs updated = teilnehmerKursService.updateEnrollmentStatus(
                teilnehmerId, kursId, request.getStatus());

        return ResponseEntity.ok(Map.of(
                "message", "Enrollment status updated successfully",
                "teilnehmerId", teilnehmerId,
                "kursId", kursId,
                "status", updated.getStatus(),
                "abmeldedatum", updated.getAbmeldedatum()
        ));
    }

    /**
     * Check if a student is enrolled in a course
     * GET /api/v1/kurse/{kursId}/teilnehmer/{teilnehmerId}/enrolled
     */
    @GetMapping("/{kursId}/teilnehmer/{teilnehmerId}/enrolled")
    public ResponseEntity<Map<String, Object>> checkEnrollment(
            @PathVariable Integer kursId,
            @PathVariable Integer teilnehmerId) {

        boolean isEnrolled = teilnehmerKursService.isStudentEnrolledInCourse(teilnehmerId, kursId);

        Map<String, Object> response = Map.of(
                "teilnehmerId", teilnehmerId,
                "kursId", kursId,
                "isEnrolled", isEnrolled
        );

        if (isEnrolled) {
            TeilnehmerKurs enrollment = teilnehmerKursService.getEnrollmentDetails(teilnehmerId, kursId);
            response = Map.of(
                    "teilnehmerId", teilnehmerId,
                    "kursId", kursId,
                    "isEnrolled", true,
                    "enrollmentDetails", Map.of(
                            "anmeldedatum", enrollment.getAnmeldedatum(),
                            "status", enrollment.getStatus(),
                            "abmeldedatum", enrollment.getAbmeldedatum(),
                            "bemerkungen", enrollment.getBemerkungen()
                    )
            );
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get enrollment details
     * GET /api/v1/kurse/{kursId}/teilnehmer/{teilnehmerId}/details
     */
    @GetMapping("/{kursId}/teilnehmer/{teilnehmerId}/details")
    public ResponseEntity<TeilnehmerKurs> getEnrollmentDetails(
            @PathVariable Integer kursId,
            @PathVariable Integer teilnehmerId) {

        TeilnehmerKurs enrollment = teilnehmerKursService.getEnrollmentDetails(teilnehmerId, kursId);
        return ResponseEntity.ok(enrollment);
    }
}