package com.bildungsinsitut.deutschkurse.service;

import com.bildungsinsitut.deutschkurse.dto.TeilnehmerDto;
import com.bildungsinsitut.deutschkurse.dto.KursDto;
import com.bildungsinsitut.deutschkurse.enums.TeilnehmerKursStatus;
import com.bildungsinsitut.deutschkurse.exception.ResourceNotFoundException;
import com.bildungsinsitut.deutschkurse.mapper.TeilnehmerMapper;
import com.bildungsinsitut.deutschkurse.mapper.KursMapper;
import com.bildungsinsitut.deutschkurse.model.*;
import com.bildungsinsitut.deutschkurse.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeilnehmerKursService {

    private final TeilnehmerKursRepository teilnehmerKursRepository;
    private final TeilnehmerRepository teilnehmerRepository;
    private final KursRepository kursRepository;
    private final TeilnehmerMapper teilnehmerMapper;
    private final KursMapper kursMapper;

    /**
     * Enroll a student in a course
     */
    public TeilnehmerKurs enrollTeilnehmerInKurs(Integer teilnehmerId, Integer kursId) {
        log.info("Enrolling student {} in course {}", teilnehmerId, kursId);

        // Check if student exists
        Teilnehmer teilnehmer = teilnehmerRepository.findById(teilnehmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Teilnehmer not found with id: " + teilnehmerId));

        // Check if course exists
        Kurs kurs = kursRepository.findById(kursId)
                .orElseThrow(() -> new ResourceNotFoundException("Kurs not found with id: " + kursId));

        // Check if already enrolled
        Optional<TeilnehmerKurs> existingEnrollment = teilnehmerKursRepository
                .findByTeilnehmerIdAndKursId(teilnehmerId, kursId);

        if (existingEnrollment.isPresent()) {
            throw new IllegalStateException("Student is already enrolled in this course");
        }

        // Check course capacity
        if (kurs.getAktuelleTeilnehmer() >= kurs.getMaxTeilnehmer()) {
            throw new IllegalStateException("Course is at maximum capacity");
        }

        // Create enrollment
        TeilnehmerKurs teilnehmerKurs = new TeilnehmerKurs();
        teilnehmerKurs.setTeilnehmer(teilnehmer);
        teilnehmerKurs.setKurs(kurs);
        teilnehmerKurs.setAnmeldedatum(LocalDate.now());
        teilnehmerKurs.setStatus(TeilnehmerKursStatus.angemeldet);

        // Save enrollment
        TeilnehmerKurs saved = teilnehmerKursRepository.save(teilnehmerKurs);

        // Update course participant count
        kurs.setAktuelleTeilnehmer(kurs.getAktuelleTeilnehmer() + 1);
        kursRepository.save(kurs);

        log.info("Successfully enrolled student {} in course {}", teilnehmerId, kursId);
        return saved;
    }

    /**
     * Remove a student from a course
     */
    public void removeTeilnehmerFromKurs(Integer teilnehmerId, Integer kursId) {
        log.info("Removing student {} from course {}", teilnehmerId, kursId);

        // Find the enrollment
        TeilnehmerKurs teilnehmerKurs = teilnehmerKursRepository
                .findByTeilnehmerIdAndKursId(teilnehmerId, kursId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrollment not found for student " + teilnehmerId + " in course " + kursId));

        // Get the course to update participant count
        Kurs kurs = teilnehmerKurs.getKurs();

        // Set withdrawal date and update status
        teilnehmerKurs.setAbmeldedatum(LocalDate.now());
        teilnehmerKurs.setStatus(TeilnehmerKursStatus.abgebrochen);
        teilnehmerKursRepository.save(teilnehmerKurs);

        // Update course participant count
        if (kurs.getAktuelleTeilnehmer() > 0) {
            kurs.setAktuelleTeilnehmer(kurs.getAktuelleTeilnehmer() - 1);
            kursRepository.save(kurs);
        }

        log.info("Successfully removed student {} from course {}", teilnehmerId, kursId);
    }

    /**
     * Get all students in a course
     */
    @Transactional(readOnly = true)
    public List<TeilnehmerDto> getTeilnehmerInKurs(Integer kursId) {
        List<TeilnehmerKurs> enrollments = teilnehmerKursRepository.findByKursId(kursId);
        return enrollments.stream()
                .filter(tk -> tk.getStatus() == TeilnehmerKursStatus.angemeldet ||
                        tk.getStatus() == TeilnehmerKursStatus.aktiv)
                .map(TeilnehmerKurs::getTeilnehmer)
                .map(teilnehmerMapper::toDto)
                .toList();
    }

    /**
     * Get all courses for a student
     */
    @Transactional(readOnly = true)
    public List<KursDto> getKurseForTeilnehmer(Integer teilnehmerId) {
        List<TeilnehmerKurs> enrollments = teilnehmerKursRepository.findByTeilnehmerId(teilnehmerId);
        return enrollments.stream()
                .filter(tk -> tk.getStatus() == TeilnehmerKursStatus.angemeldet ||
                        tk.getStatus() == TeilnehmerKursStatus.aktiv)
                .map(TeilnehmerKurs::getKurs)
                .map(kursMapper::toDto)
                .toList();
    }

    /**
     * Update enrollment status (e.g., from angemeldet to aktiv)
     */
    public TeilnehmerKurs updateEnrollmentStatus(Integer teilnehmerId, Integer kursId,
                                                 TeilnehmerKursStatus newStatus) {
        TeilnehmerKurs teilnehmerKurs = teilnehmerKursRepository
                .findByTeilnehmerIdAndKursId(teilnehmerId, kursId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrollment not found for student " + teilnehmerId + " in course " + kursId));

        teilnehmerKurs.setStatus(newStatus);

        // Set appropriate dates based on status
        if (newStatus == TeilnehmerKursStatus.abgeschlossen ||
                newStatus == TeilnehmerKursStatus.abgebrochen) {
            teilnehmerKurs.setAbmeldedatum(LocalDate.now());
        }

        return teilnehmerKursRepository.save(teilnehmerKurs);
    }

    /**
     * Check if a student is enrolled in a course
     */
    @Transactional(readOnly = true)
    public boolean isStudentEnrolledInCourse(Integer teilnehmerId, Integer kursId) {
        Optional<TeilnehmerKurs> enrollment = teilnehmerKursRepository
                .findByTeilnehmerIdAndKursId(teilnehmerId, kursId);

        return enrollment.isPresent() &&
                (enrollment.get().getStatus() == TeilnehmerKursStatus.angemeldet ||
                        enrollment.get().getStatus() == TeilnehmerKursStatus.aktiv);
    }

    /**
     * Get enrollment details
     */
    @Transactional(readOnly = true)
    public TeilnehmerKurs getEnrollmentDetails(Integer teilnehmerId, Integer kursId) {
        return teilnehmerKursRepository.findByTeilnehmerIdAndKursId(teilnehmerId, kursId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrollment not found for student " + teilnehmerId + " in course " + kursId));
    }
}