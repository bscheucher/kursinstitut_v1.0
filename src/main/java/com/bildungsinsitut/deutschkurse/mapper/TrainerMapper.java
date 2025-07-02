package com.bildungsinsitut.deutschkurse.mapper;

import com.bildungsinsitut.deutschkurse.dto.TrainerDto;
import com.bildungsinsitut.deutschkurse.model.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainerMapper {

    TrainerDto toDto(Trainer trainer);

    @Mapping(target = "erstelltAm", ignore = true)
    @Mapping(target = "geaendertAm", ignore = true)
    Trainer toEntity(TrainerDto trainerDto);

    List<TrainerDto> toDtoList(List<Trainer> trainers);
}