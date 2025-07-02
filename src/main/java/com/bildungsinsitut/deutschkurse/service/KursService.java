package com.bildungsinsitut.deutschkurse.service;

import com.bildungsinsitut.deutschkurse.dto.KursDto;
import com.bildungsinsitut.deutschkurse.exception.ResourceNotFoundException;
import com.bildungsinsitut.deutschkurse.mapper.KursMapper;
import com.bildungsinsitut.deutschkurse.model.Kurs;
import com.bildungsinsitut.deutschkurse.repository.KursRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KursService {

    private final KursRepository kursRepository;
    private final KursMapper kursMapper;

    public KursService(KursRepository kursRepository, KursMapper kursMapper) {
        this.kursRepository = kursRepository;
        this.kursMapper = kursMapper;
    }

    public List<KursDto> getAllKurse() {
        return kursRepository.findAll().stream()
                .map(kursMapper::toDto)
                .collect(Collectors.toList());
    }

    public KursDto getKursById(Long id) {
        Kurs kurs = kursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kurs not found with id: " + id));
        return kursMapper.toDto(kurs);
    }

    // Add create, update, and delete methods here
}