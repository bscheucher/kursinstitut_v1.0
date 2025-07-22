package com.bildungsinsitut.deutschkurse.service;

import com.bildungsinsitut.deutschkurse.dto.StundenplanDto;
import com.bildungsinsitut.deutschkurse.exception.ResourceNotFoundException;
import com.bildungsinsitut.deutschkurse.mapper.StundenplanMapper;
import com.bildungsinsitut.deutschkurse.model.Kurs;
import com.bildungsinsitut.deutschkurse.model.Stundenplan;
import com.bildungsinsitut.deutschkurse.repository.KursRepository;
import com.bildungsinsitut.deutschkurse.repository.StundenplanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StundenplanService {

    private final StundenplanRepository stundenplanRepository;
    private final KursRepository kursRepository;
    private final StundenplanMapper stundenplanMapper;

    /**
     * Get all schedules
     */
    @Transactional(readOnly = true)
    public List<StundenplanDto> getAllStundenplaene() {
        return stundenplanMapper.toDtoList(stundenplanRepository.findByAktivTrue());
    }

    /**
     * Get schedule by ID
     */
    @Transactional(readOnly = true)
    public StundenplanDto getStundenplanById(Integer id) {
        Stundenplan stundenplan = stundenplanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stundenplan not found with id: " + id));
        return stundenplanMapper.toDto(stundenplan);
    }

    /**
     * Get schedules for a specific course
     */
    @Transactional(readOnly = true)
    public List<StundenplanDto> getStundenplaeneByKurs(Integer kursId) {
        return stundenplanMapper.toDtoList(stundenplanRepository.findByKursId(kursId));
    }

    /**
     * Get schedules for a specific weekday
     */
    @Transactional(readOnly = true)
    public List<StundenplanDto> getStundenplaeneByWochentag(String wochentag) {
        return stundenplanMapper.toDtoList(stundenplanRepository.findByWochentag(wochentag));
    }

    /**
     * Create a new schedule entry
     */
    public StundenplanDto createStundenplan(StundenplanDto stundenplanDto) {
        // Validate time range
        if (!stundenplanDto.isValidTimeRange()) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Check if course exists
        Kurs kurs = kursRepository.findById(stundenplanDto.getKursId())
                .orElseThrow(() -> new ResourceNotFoundException("Kurs not found with id: " + stundenplanDto.getKursId()));

        // Check for scheduling conflicts
        List<Stundenplan> existingSchedules = stundenplanRepository.findByKursId(stundenplanDto.getKursId());
        for (Stundenplan existing : existingSchedules) {
            if (existing.getWochentag().equals(stundenplanDto.getWochentag()) &&
                    existing.getAktiv() &&
                    hasTimeConflict(existing, stundenplanDto)) {
                throw new IllegalStateException("Time conflict detected for this course on " + stundenplanDto.getWochentag());
            }
        }

        Stundenplan stundenplan = stundenplanMapper.toEntity(stundenplanDto);
        stundenplan.setKurs(kurs);

        stundenplan = stundenplanRepository.save(stundenplan);
        log.info("Created new Stundenplan with id: {} for Kurs: {}", stundenplan.getId(), kurs.getKursName());

        return stundenplanMapper.toDto(stundenplan);
    }

    /**
     * Update an existing schedule entry
     */
    public StundenplanDto updateStundenplan(Integer id, StundenplanDto stundenplanDto) {
        Stundenplan stundenplan = stundenplanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stundenplan not found with id: " + id));

        // Validate time range
        if (!stundenplanDto.isValidTimeRange()) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Update fields
        stundenplan.setWochentag(stundenplanDto.getWochentag());
        stundenplan.setStartzeit(stundenplanDto.getStartzeit());
        stundenplan.setEndzeit(stundenplanDto.getEndzeit());
        stundenplan.setBemerkungen(stundenplanDto.getBemerkungen());
        stundenplan.setAktiv(stundenplanDto.getAktiv());

        // Update course if changed
        if (!stundenplan.getKurs().getId().equals(stundenplanDto.getKursId())) {
            Kurs kurs = kursRepository.findById(stundenplanDto.getKursId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kurs not found with id: " + stundenplanDto.getKursId()));
            stundenplan.setKurs(kurs);
        }

        stundenplan = stundenplanRepository.save(stundenplan);
        log.info("Updated Stundenplan with id: {}", id);

        return stundenplanMapper.toDto(stundenplan);
    }

    /**
     * Delete a schedule entry
     */
    public void deleteStundenplan(Integer id) {
        Stundenplan stundenplan = stundenplanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stundenplan not found with id: " + id));

        // Soft delete - set aktiv to false
        stundenplan.setAktiv(false);
        stundenplanRepository.save(stundenplan);
        log.info("Soft deleted Stundenplan with id: {}", id);
    }

    /**
     * Check if there's a time conflict between two schedule entries
     */
    private boolean hasTimeConflict(Stundenplan existing, StundenplanDto newSchedule) {
        return !(newSchedule.getEndzeit().isBefore(existing.getStartzeit()) ||
                newSchedule.getStartzeit().isAfter(existing.getEndzeit()));
    }
}