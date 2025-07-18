package com.bildungsinsitut.deutschkurse.service;

import com.bildungsinsitut.deutschkurse.dto.TeilnehmerDto;
import com.bildungsinsitut.deutschkurse.exception.ResourceNotFoundException;
import com.bildungsinsitut.deutschkurse.mapper.TeilnehmerMapper;
import com.bildungsinsitut.deutschkurse.model.Teilnehmer;
import com.bildungsinsitut.deutschkurse.repository.TeilnehmerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeilnehmerService {

    private final TeilnehmerRepository teilnehmerRepository;
    private final TeilnehmerMapper teilnehmerMapper;

    @Transactional(readOnly = true)
    public List<TeilnehmerDto> getAllTeilnehmer() {
        return teilnehmerMapper.toDtoList(teilnehmerRepository.findByAktivTrue());
    }

    @Transactional(readOnly = true)
    public TeilnehmerDto getTeilnehmerById(Integer id) {
        Teilnehmer teilnehmer = teilnehmerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teilnehmer not found with id: " + id));
        return teilnehmerMapper.toDto(teilnehmer);
    }

    public TeilnehmerDto createTeilnehmer(TeilnehmerDto teilnehmerDto) {
        Teilnehmer teilnehmer = teilnehmerMapper.toEntity(teilnehmerDto);
        teilnehmer = teilnehmerRepository.save(teilnehmer);
        log.info("Created new Teilnehmer with id: {}", teilnehmer.getId());
        return teilnehmerMapper.toDto(teilnehmer);
    }

    public TeilnehmerDto updateTeilnehmer(Integer id, TeilnehmerDto teilnehmerDto) {
        Teilnehmer teilnehmer = teilnehmerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teilnehmer not found with id: " + id));

        // Update fields
        teilnehmer.setVorname(teilnehmerDto.getVorname());
        teilnehmer.setNachname(teilnehmerDto.getNachname());
        teilnehmer.setEmail(teilnehmerDto.getEmail());
        teilnehmer.setTelefon(teilnehmerDto.getTelefon());
        teilnehmer.setGeburtsdatum(teilnehmerDto.getGeburtsdatum());
        teilnehmer.setGeschlecht(teilnehmerDto.getGeschlecht());
        teilnehmer.setStaatsangehoerigkeit(teilnehmerDto.getStaatsangehoerigkeit());
        teilnehmer.setMuttersprache(teilnehmerDto.getMuttersprache());
        teilnehmer.setAnmeldedatum(teilnehmerDto.getAnmeldedatum());
        teilnehmer.setAktiv(teilnehmerDto.getAktiv());

        teilnehmer = teilnehmerRepository.save(teilnehmer);
        log.info("Updated Teilnehmer with id: {}", id);
        return teilnehmerMapper.toDto(teilnehmer);
    }

    public void deleteTeilnehmer(Integer id) {
        Teilnehmer teilnehmer = teilnehmerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teilnehmer not found with id: " + id));

        // Soft delete - set aktiv to false
        teilnehmer.setAktiv(false);
        teilnehmerRepository.save(teilnehmer);
        log.info("Soft deleted Teilnehmer with id: {}", id);
    }

    @Transactional(readOnly = true)
    public List<TeilnehmerDto> searchTeilnehmerByName(String name) {
        List<Teilnehmer> teilnehmer = teilnehmerRepository
                .findByVornameContainingIgnoreCaseOrNachnameContainingIgnoreCase(name, name);
        return teilnehmerMapper.toDtoList(teilnehmer);
    }

    @Transactional(readOnly = true)
    public List<TeilnehmerDto> searchTeilnehmerByEmail(String email) {
        List<Teilnehmer> teilnehmer = teilnehmerRepository.findByEmailContainingIgnoreCase(email);
        return teilnehmerMapper.toDtoList(teilnehmer);
    }
}