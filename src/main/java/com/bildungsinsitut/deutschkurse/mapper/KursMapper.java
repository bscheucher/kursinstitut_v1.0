package com.bildungsinsitut.deutschkurse.mapper;

import com.bildungsinsitut.deutschkurse.dto.KursDto;
import com.bildungsinsitut.deutschkurse.model.Kurs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface KursMapper {

    @Mapping(source = "kurstyp.id", target = "kurstypId")
    @Mapping(source = "kurstyp.kurstypName", target = "kurstypName")
    @Mapping(source = "kursraum.id", target = "kursraumId")
    @Mapping(source = "kursraum.raumName", target = "kursraumName")
    @Mapping(source = "trainer.id", target = "trainerId")
    @Mapping(source = "trainer.vorname", target = "trainerName",
            expression = "java(kurs.getTrainer().getVorname() + \" \" + kurs.getTrainer().getNachname())")
    KursDto toDto(Kurs kurs);

    @Mapping(source = "kurstypId", target = "kurstyp.id")
    @Mapping(source = "kursraumId", target = "kursraum.id")
    @Mapping(source = "trainerId", target = "trainer.id")
    Kurs toEntity(KursDto kursDto);

    List<KursDto> toDtoList(List<Kurs> kurse);
}