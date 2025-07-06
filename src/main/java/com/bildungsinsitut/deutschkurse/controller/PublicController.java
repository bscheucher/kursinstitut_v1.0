package com.bildungsinsitut.deutschkurse.controller;

import com.bildungsinsitut.deutschkurse.dto.TrainerDto;
import com.bildungsinsitut.deutschkurse.service.TrainerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
public class PublicController {

    private final TrainerService trainerService;

    public PublicController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping("/trainer/abteilung/{abteilungId}")
    public ResponseEntity<List<TrainerDto>> getTrainerByAbteilung(@PathVariable Integer abteilungId) {
        // This method should return DTOs, not entities
        List<TrainerDto> trainers = trainerService.getTrainerDtoByAbteilung(abteilungId);
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/trainer")
    public ResponseEntity<List<TrainerDto>> getAllPublicTrainer() {
        return ResponseEntity.ok(trainerService.getAllTrainer());
    }

    @GetMapping("/trainer/verfuegbar")
    public ResponseEntity<List<TrainerDto>> getVerfuegbareTrainer() {
        return ResponseEntity.ok(trainerService.getVerfuegbareTrainer());
    }

    @PostMapping("/trainer")
    public ResponseEntity<TrainerDto> createTrainer(@Valid @RequestBody TrainerDto trainerDto) {
        TrainerDto created = trainerService.createTrainer(trainerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/trainer/{id}")
    public ResponseEntity<TrainerDto> updateTrainer(@PathVariable Integer id,
                                                    @Valid @RequestBody TrainerDto trainerDto) {
        return ResponseEntity.ok(trainerService.updateTrainer(id, trainerDto));
    }

    @DeleteMapping("trainer/{id}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Integer id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }
}