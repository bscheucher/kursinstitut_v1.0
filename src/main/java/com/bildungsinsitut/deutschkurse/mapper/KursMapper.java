package com.bildungsinsitut.deutschkurse.mapper;

import com.bildungsinsitut.deutschkurse.dto.KursDto;
import com.bildungsinsitut.deutschkurse.model.Kurs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface KursMapper {

    @Mapping(source = "kurstyp.id", target = "kurstypId")
    @Mapping(source = "kurstyp.kurstypName", target = "kurstypName")
    @Mapping(source = "kursraum.id", target = "kursraumId")
    @Mapping(source = "kursraum.raumName", target = "kursraumName")
    @Mapping(source = "trainer.id", target = "trainerId")
    @Mapping(source = "trainer", target = "trainerName", qualifiedByName = "trainerToFullName")
    KursDto toDto(Kurs kurs);

    @Mapping(source = "kurstypId", target = "kurstyp.id")
    @Mapping(source = "kursraumId", target = "kursraum.id")
    @Mapping(source = "trainerId", target = "trainer.id")
    @Mapping(target = "kurstyp", ignore = true)
    @Mapping(target = "kursraum", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "teilnehmerKurse", ignore = true)
    @Mapping(target = "stundenplaene", ignore = true)
    @Mapping(target = "anwesenheiten", ignore = true)
    @Mapping(target = "erstelltAm", ignore = true)
    @Mapping(target = "geaendertAm", ignore = true)
    Kurs toEntity(KursDto kursDto);

    List<KursDto> toDtoList(List<Kurs> kurse);

    @Named("trainerToFullName")
    default String trainerToFullName(com.bildungsinsitut.deutschkurse.model.Trainer trainer) {
        if (trainer == null) {
            return null;
        }
        return trainer.getVorname() + " " + trainer.getNachname();
    }
}
