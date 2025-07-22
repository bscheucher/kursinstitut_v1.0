package com.bildungsinsitut.deutschkurse.service;

import com.bildungsinsitut.deutschkurse.dto.AnwesenheitDto;
import com.bildungsinsitut.deutschkurse.dto.BulkAnwesenheitDto;
import com.bildungsinsitut.deutschkurse.exception.ResourceNotFoundException;
import com.bildungsinsitut.deutschkurse.mapper.AnwesenheitMapper;
import com.bildungsinsitut.deutschkurse.model.*;
import com.bildungsinsitut.deutschkurse.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AnwesenheitService {

    private final AnwesenheitRepository anwesenheitRepository;
    private final TeilnehmerRepository teilnehmerRepository;
    private final KursRepository kursRepository;
    private final TeilnehmerKursRepository teilnehmerKursRepository;
    private final AnwesenheitMapper anwesenheitMapper;

    /**
     * Get all attendance records
     */
    @Transactional(readOnly = true)
    public List<AnwesenheitDto> getAllAnwesenheiten() {
        return anwesenheitMapper.toDtoList(anwesenheitRepository.findAll());
    }

    /**
     * Get attendance by ID
     */
    @Transactional(readOnly = true)
    public AnwesenheitDto getAnwesenheitById(Integer id) {
        Anwesenheit anwesenheit = anwesenheitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anwesenheit not found with id: " + id));
        return anwesenheitMapper.toDto(anwesenheit);
    }

    /**
     * Get attendance for a specific course on a specific date
     */
    @Transactional(readOnly = true)
    public List<AnwesenheitDto> getAnwesenheitByKursAndDatum(Integer kursId, LocalDate datum) {
        return anwesenheitMapper.toDtoList(anwesenheitRepository.findByKursIdAndDatum(kursId, datum));
    }

    /**
     * Get attendance history for a specific student in a specific course
     */
    @Transactional(readOnly = true)
    public List<AnwesenheitDto> getAnwesenheitByTeilnehmerAndKurs(Integer teilnehmerId, Integer kursId) {
        return anwesenheitMapper.toDtoList(anwesenheitRepository.findByTeilnehmerIdAndKursId(teilnehmerId, kursId));
    }

    /**
     * Get attendance statistics for a student in a course
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAnwesenheitStatistik(Integer teilnehmerId, Integer kursId) {
        List<Anwesenheit> attendanceList = anwesenheitRepository.findByTeilnehmerIdAndKursId(teilnehmerId, kursId);

        long totalDays = attendanceList.size();
        long presentDays = attendanceList.stream().filter(Anwesenheit::getAnwesend).count();
        long excusedDays = attendanceList.stream().filter(a -> !a.getAnwesend() && a.getEntschuldigt()).count();
        long unexcusedDays = attendanceList.stream().filter(a -> !a.getAnwesend() && !a.getEntschuldigt()).count();

        double attendanceRate = totalDays > 0 ? (double) presentDays / totalDays * 100 : 0.0;

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalDays", totalDays);
        statistics.put("presentDays", presentDays);
        statistics.put("excusedDays", excusedDays);
        statistics.put("unexcusedDays", unexcusedDays);
        statistics.put("attendanceRate", Math.round(attendanceRate * 100.0) / 100.0);

        return statistics;
    }

    /**
     * Create or update attendance for a single student
     */
    public AnwesenheitDto createOrUpdateAnwesenheit(AnwesenheitDto anwesenheitDto) {
        // Verify student exists
        Teilnehmer teilnehmer = teilnehmerRepository.findById(anwesenheitDto.getTeilnehmerId())
                .orElseThrow(() -> new ResourceNotFoundException("Teilnehmer not found with id: " + anwesenheitDto.getTeilnehmerId()));

        // Verify course exists
        Kurs kurs = kursRepository.findById(anwesenheitDto.getKursId())
                .orElseThrow(() -> new ResourceNotFoundException("Kurs not found with id: " + anwesenheitDto.getKursId()));

        // Verify student is enrolled in the course
        teilnehmerKursRepository.findByTeilnehmerIdAndKursId(teilnehmer.getId(), kurs.getId())
                .orElseThrow(() -> new IllegalStateException("Student is not enrolled in this course"));

        // Check if attendance already exists for this student, course, and date
        Optional<Anwesenheit> existingAnwesenheit = anwesenheitRepository.findByTeilnehmerIdAndKursId(
                        teilnehmer.getId(), kurs.getId()).stream()
                .filter(a -> a.getDatum().equals(anwesenheitDto.getDatum()))
                .findFirst();

        Anwesenheit anwesenheit;
        if (existingAnwesenheit.isPresent()) {
            // Update existing attendance
            anwesenheit = existingAnwesenheit.get();
            anwesenheit.setAnwesend(anwesenheitDto.getAnwesend());
            anwesenheit.setEntschuldigt(anwesenheitDto.getEntschuldigt());
            anwesenheit.setBemerkung(anwesenheitDto.getBemerkung());
            log.info("Updated attendance for Teilnehmer {} in Kurs {} on {}",
                    teilnehmer.getId(), kurs.getId(), anwesenheitDto.getDatum());
        } else {
            // Create new attendance
            anwesenheit = anwesenheitMapper.toEntity(anwesenheitDto);
            anwesenheit.setTeilnehmer(teilnehmer);
            anwesenheit.setKurs(kurs);
            log.info("Created new attendance for Teilnehmer {} in Kurs {} on {}",
                    teilnehmer.getId(), kurs.getId(), anwesenheitDto.getDatum());
        }

        anwesenheit = anwesenheitRepository.save(anwesenheit);
        return anwesenheitMapper.toDto(anwesenheit);
    }

    /**
     * Bulk create/update attendance for multiple students
     */
    public List<AnwesenheitDto> createBulkAnwesenheit(BulkAnwesenheitDto bulkDto) {
        // Verify course exists
        Kurs kurs = kursRepository.findById(bulkDto.getKursId())
                .orElseThrow(() -> new ResourceNotFoundException("Kurs not found with id: " + bulkDto.getKursId()));

        List<AnwesenheitDto> results = new ArrayList<>();

        for (BulkAnwesenheitDto.AttendanceRecord record : bulkDto.getAttendanceRecords()) {
            AnwesenheitDto dto = new AnwesenheitDto();
            dto.setTeilnehmerId(record.getTeilnehmerId());
            dto.setKursId(bulkDto.getKursId());
            dto.setDatum(bulkDto.getDatum());
            dto.setAnwesend(record.getAnwesend());
            dto.setEntschuldigt(record.getEntschuldigt());
            dto.setBemerkung(record.getBemerkung());

            try {
                results.add(createOrUpdateAnwesenheit(dto));
            } catch (Exception e) {
                log.error("Failed to create attendance for Teilnehmer {}: {}",
                        record.getTeilnehmerId(), e.getMessage());
            }
        }

        log.info("Bulk attendance created/updated for {} students in Kurs {} on {}",
                results.size(), kurs.getId(), bulkDto.getDatum());

        return results;
    }

    /**
     * Delete attendance record
     */
    public void deleteAnwesenheit(Integer id) {
        if (!anwesenheitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Anwesenheit not found with id: " + id);
        }
        anwesenheitRepository.deleteById(id);
        log.info("Deleted Anwesenheit with id: {}", id);
    }

    /**
     * Get attendance for date range
     */
    @Transactional(readOnly = true)
    public List<AnwesenheitDto> getAnwesenheitByDateRange(LocalDate startDate, LocalDate endDate) {
        return anwesenheitMapper.toDtoList(anwesenheitRepository.findByDatumBetween(startDate, endDate));
    }
}