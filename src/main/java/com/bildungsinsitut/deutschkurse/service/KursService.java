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

    public KursDto getKursById(Long id) {
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

    public KursDto createKurs(KursDto kursDto) {
        Kurs kurs = kursMapper.toEntity(kursDto);

        // Set relationships
        kurs.setKurstyp(kurstypRepository.findById(kursDto.getKurstypId())
                .orElseThrow(() -> new ResourceNotFoundException("Kurstyp not found")));
        kurs.setKursraum(kursraumRepository.findById(kursDto.getKursraumId())
                .orElseThrow(() -> new ResourceNotFoundException("Kursraum not found")));
        kurs.setTrainer(trainerRepository.findById(kursDto.getTrainerId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found")));

        kurs = kursRepository.save(kurs);
        return kursMapper.toDto(kurs);
    }

    public KursDto updateKurs(Long id, KursDto kursDto) {
        Kurs kurs = kursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kurs not found with id: " + id));

        // Update fields
        kurs.setKursName(kursDto.getKursName());
        kurs.setStartdatum(kursDto.getStartdatum());
        kurs.setEnddatum(kursDto.getEnddatum());
        kurs.setMaxTeilnehmer(kursDto.getMaxTeilnehmer());
        kurs.setStatus(kursDto.getStatus());
        kurs.setBeschreibung(kursDto.getBeschreibung());

        kurs = kursRepository.save(kurs);
        return kursMapper.toDto(kurs);
    }

    public void deleteKurs(Long id) {
        if (!kursRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kurs not found with id: " + id);
        }
        kursRepository.deleteById(id);
    }
}