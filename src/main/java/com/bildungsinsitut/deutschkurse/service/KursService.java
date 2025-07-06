package com.bildungsinsitut.deutschkurse.service;

import com.bildungsinsitut.deutschkurse.dto.KursDto;
import com.bildungsinsitut.deutschkurse.enums.KursStatusType;
import com.bildungsinsitut.deutschkurse.exception.ResourceNotFoundException;
import com.bildungsinsitut.deutschkurse.mapper.KursMapper;
import com.bildungsinsitut.deutschkurse.model.Kurs;
import com.bildungsinsitut.deutschkurse.repository.KursRepository;
import com.bildungsinsitut.deutschkurse.repository.KurstypRepository;
import com.bildungsinsitut.deutschkurse.repository.KursraumRepository;
import com.bildungsinsitut.deutschkurse.repository.TrainerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class KursService {

    private final KursRepository kursRepository;
    private final KursMapper kursMapper;
    private final KurstypRepository kurstypRepository;
    private final KursraumRepository kursraumRepository;
    private final TrainerRepository trainerRepository;

    public KursService(KursRepository kursRepository, KursMapper kursMapper,
                       KurstypRepository kurstypRepository, KursraumRepository kursraumRepository,
                       TrainerRepository trainerRepository) {
        this.kursRepository = kursRepository;
        this.kursMapper = kursMapper;
        this.kurstypRepository = kurstypRepository;
        this.kursraumRepository = kursraumRepository;
        this.trainerRepository = trainerRepository;
    }

    public List<KursDto> getAllKurse() {
        return kursMapper.toDtoList(kursRepository.findAll());
    }

    public KursDto getKursById(Integer id) {
        Kurs kurs = kursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kurs not found with id: " + id));
        return kursMapper.toDto(kurs);
    }

    public List<KursDto> getKurseByStatus(KursStatusType status) {
        return kursMapper.toDtoList(kursRepository.findByStatus(status));
    }

    public List<KursDto> getAktuelleKurse() {
        return kursMapper.toDtoList(kursRepository.findByStatusIn(
                List.of(KursStatusType.geplant, KursStatusType.laufend)));
    }

    /**
     * Get courses by trainer ID
     */
    public List<KursDto> getKurseByTrainer(Integer trainerId) {
        return kursMapper.toDtoList(kursRepository.findByTrainerId(trainerId));
    }

    /**
     * Get available courses (with available spots)
     */
    public List<KursDto> getVerfuegbareKurse() {
        return kursMapper.toDtoList(kursRepository.findVerfuegbareKurse());
    }

    /**
     * Get courses starting within a date range
     */
    public List<KursDto> getKurseByStartdatumRange(LocalDate startDate, LocalDate endDate) {
        return kursMapper.toDtoList(kursRepository.findByStartdatumBetween(startDate, endDate));
    }

    public KursDto createKurs(KursDto kursDto) {
        // Validate date range
        if (kursDto.getStartdatum() != null && kursDto.getEnddatum() != null) {
            if (!kursDto.getEnddatum().isAfter(kursDto.getStartdatum())) {
                throw new IllegalArgumentException("End date must be after start date");
            }
        }

        // Validate participant count
        if (kursDto.getAktuelleTeilnehmer() != null && kursDto.getMaxTeilnehmer() != null) {
            if (kursDto.getAktuelleTeilnehmer() > kursDto.getMaxTeilnehmer()) {
                throw new IllegalArgumentException("Current participants cannot exceed maximum participants");
            }
        }

        Kurs kurs = kursMapper.toEntity(kursDto);

        // Set relationships
        kurs.setKurstyp(kurstypRepository.findById(kursDto.getKurstypId())
                .orElseThrow(() -> new ResourceNotFoundException("Kurstyp not found with id: " + kursDto.getKurstypId())));
        kurs.setKursraum(kursraumRepository.findById(kursDto.getKursraumId())
                .orElseThrow(() -> new ResourceNotFoundException("Kursraum not found with id: " + kursDto.getKursraumId())));
        kurs.setTrainer(trainerRepository.findById(kursDto.getTrainerId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + kursDto.getTrainerId())));

        kurs = kursRepository.save(kurs);
        return kursMapper.toDto(kurs);
    }

    public KursDto updateKurs(Integer id, KursDto kursDto) {
        Kurs kurs = kursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kurs not found with id: " + id));

        // Validate date range if both dates are provided
        if (kursDto.getStartdatum() != null && kursDto.getEnddatum() != null) {
            if (!kursDto.getEnddatum().isAfter(kursDto.getStartdatum())) {
                throw new IllegalArgumentException("End date must be after start date");
            }
        }

        // Update fields
        kurs.setKursName(kursDto.getKursName());
        kurs.setStartdatum(kursDto.getStartdatum());
        kurs.setEnddatum(kursDto.getEnddatum());
        kurs.setMaxTeilnehmer(kursDto.getMaxTeilnehmer());
        kurs.setStatus(kursDto.getStatus());
        kurs.setBeschreibung(kursDto.getBeschreibung());

        // Update relationships if provided
        if (kursDto.getKurstypId() != null) {
            kurs.setKurstyp(kurstypRepository.findById(kursDto.getKurstypId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kurstyp not found with id: " + kursDto.getKurstypId())));
        }
        if (kursDto.getKursraumId() != null) {
            kurs.setKursraum(kursraumRepository.findById(kursDto.getKursraumId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kursraum not found with id: " + kursDto.getKursraumId())));
        }
        if (kursDto.getTrainerId() != null) {
            kurs.setTrainer(trainerRepository.findById(kursDto.getTrainerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + kursDto.getTrainerId())));
        }

        kurs = kursRepository.save(kurs);
        return kursMapper.toDto(kurs);
    }

    /**
     * Update only the status of a course
     */
    public KursDto updateKursStatus(Integer id, KursStatusType status) {
        Kurs kurs = kursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kurs not found with id: " + id));

        kurs.setStatus(status);
        kurs = kursRepository.save(kurs);
        return kursMapper.toDto(kurs);
    }

    /**
     * Add a participant to a course (increment current participants)
     */
    public KursDto addTeilnehmerToKurs(Integer kursId) {
        Kurs kurs = kursRepository.findById(kursId)
                .orElseThrow(() -> new ResourceNotFoundException("Kurs not found with id: " + kursId));

        if (kurs.getAktuelleTeilnehmer() >= kurs.getMaxTeilnehmer()) {
            throw new IllegalStateException("Course is already at maximum capacity");
        }

        kurs.setAktuelleTeilnehmer(kurs.getAktuelleTeilnehmer() + 1);
        kurs = kursRepository.save(kurs);
        return kursMapper.toDto(kurs);
    }

    /**
     * Remove a participant from a course (decrement current participants)
     */
    public KursDto removeTeilnehmerFromKurs(Integer kursId) {
        Kurs kurs = kursRepository.findById(kursId)
                .orElseThrow(() -> new ResourceNotFoundException("Kurs not found with id: " + kursId));

        if (kurs.getAktuelleTeilnehmer() <= 0) {
            throw new IllegalStateException("Course has no participants to remove");
        }

        kurs.setAktuelleTeilnehmer(kurs.getAktuelleTeilnehmer() - 1);
        kurs = kursRepository.save(kurs);
        return kursMapper.toDto(kurs);
    }

    public void deleteKurs(Integer id) {
        if (!kursRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kurs not found with id: " + id);
        }
        kursRepository.deleteById(id);
    }
}