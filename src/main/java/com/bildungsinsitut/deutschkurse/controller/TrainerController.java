package com.bildungsinsitut.deutschkurse.controller;

import com.bildungsinsitut.deutschkurse.dto.TrainerDto;
import com.bildungsinsitut.deutschkurse.model.Trainer;
import com.bildungsinsitut.deutschkurse.service.TrainerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trainer")
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping
    public ResponseEntity<List<TrainerDto>> getAllTrainer() {
        return ResponseEntity.ok(trainerService.getAllTrainer());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainerDto> getTrainerById(@PathVariable Integer id) {
        return ResponseEntity.ok(trainerService.getTrainerById(id));
    }

    @GetMapping("/verfuegbar")
    public ResponseEntity<List<TrainerDto>> getVerfuegbareTrainer() {
        return ResponseEntity.ok(trainerService.getVerfuegbareTrainer());
    }

    @GetMapping("/abteilung/{abteilungId}")
    public ResponseEntity<List<Trainer>> getTrainerByAbteilung(@PathVariable Integer abteilungId) {
        return ResponseEntity.ok(trainerService.getTrainerByAbteilung(abteilungId));
    }

    @PostMapping
    public ResponseEntity<TrainerDto> createTrainer(@Valid @RequestBody TrainerDto trainerDto) {
        TrainerDto created = trainerService.createTrainer(trainerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainerDto> updateTrainer(@PathVariable Integer id,
                                                    @Valid @RequestBody TrainerDto trainerDto) {
        return ResponseEntity.ok(trainerService.updateTrainer(id, trainerDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Integer id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }
}