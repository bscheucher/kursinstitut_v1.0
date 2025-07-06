package com.bildungsinsitut.deutschkurse.service;

import com.bildungsinsitut.deutschkurse.dto.TrainerDto;
import com.bildungsinsitut.deutschkurse.enums.TrainerStatus;
import com.bildungsinsitut.deutschkurse.exception.ResourceNotFoundException;
import com.bildungsinsitut.deutschkurse.mapper.TrainerMapper;
import com.bildungsinsitut.deutschkurse.model.Trainer;
import com.bildungsinsitut.deutschkurse.repository.TrainerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainerMapper trainerMapper;

    public TrainerService(TrainerRepository trainerRepository, TrainerMapper trainerMapper) {
        this.trainerRepository = trainerRepository;
        this.trainerMapper = trainerMapper;
    }

    public List<TrainerDto> getAllTrainer() {
        return trainerMapper.toDtoList(trainerRepository.findByAktivTrue());
    }

    public TrainerDto getTrainerById(Integer id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));
        return trainerMapper.toDto(trainer);
    }

    public List<TrainerDto> getVerfuegbareTrainer() {
        return trainerMapper.toDtoList(
                trainerRepository.findByStatusAndAktivTrue(TrainerStatus.verfuegbar));
    }

    public TrainerDto createTrainer(TrainerDto trainerDto) {
        Trainer trainer = trainerMapper.toEntity(trainerDto);
        trainer = trainerRepository.save(trainer);
        return trainerMapper.toDto(trainer);
    }

    public TrainerDto updateTrainer(Integer id, TrainerDto trainerDto) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));

        // Update fields
        trainer.setVorname(trainerDto.getVorname());
        trainer.setNachname(trainerDto.getNachname());
        trainer.setEmail(trainerDto.getEmail());
        trainer.setTelefon(trainerDto.getTelefon());
        trainer.setStatus(trainerDto.getStatus());
        trainer.setQualifikationen(trainerDto.getQualifikationen());
        trainer.setEinstellungsdatum(trainerDto.getEinstellungsdatum());
        trainer.setAktiv(trainerDto.getAktiv());

        trainer = trainerRepository.save(trainer);
        return trainerMapper.toDto(trainer);
    }

    public void deleteTrainer(Integer id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));
        trainer.setAktiv(false);
        trainerRepository.save(trainer);
    }

    // Keep this method for backward compatibility (returns entities)
    public List<Trainer> getTrainerByAbteilung(Integer abteilungId) {
        return trainerRepository.findByAbteilungIdAndAktivTrue(abteilungId);
    }

    // NEW METHOD: Returns DTOs for public API
    public List<TrainerDto> getTrainerDtoByAbteilung(Integer abteilungId) {
        List<Trainer> trainers = trainerRepository.findByAbteilungIdAndAktivTrue(abteilungId);
        return trainerMapper.toDtoList(trainers);
    }
}