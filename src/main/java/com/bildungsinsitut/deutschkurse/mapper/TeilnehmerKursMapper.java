package com.bildungsinsitut.deutschkurse.mapper;

import com.bildungsinsitut.deutschkurse.dto.TeilnehmerKursDto;
import com.bildungsinsitut.deutschkurse.model.TeilnehmerKurs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeilnehmerKursMapper {

    @Mapping(source = "teilnehmer.id", target = "teilnehmerId")
    @Mapping(source = "teilnehmer", target = "teilnehmerName", qualifiedByName = "teilnehmerToFullName")
    @Mapping(source = "kurs.id", target = "kursId")
    @Mapping(source = "kurs.kursName", target = "kursName")
    TeilnehmerKursDto toDto(TeilnehmerKurs teilnehmerKurs);

    @Mapping(source = "teilnehmerId", target = "teilnehmer.id")
    @Mapping(source = "kursId", target = "kurs.id")
    @Mapping(target = "teilnehmer", ignore = true) // Will be set by service
    @Mapping(target = "kurs", ignore = true) // Will be set by service
    @Mapping(target = "erstelltAm", ignore = true)
    @Mapping(target = "geaendertAm", ignore = true)
    TeilnehmerKurs toEntity(TeilnehmerKursDto teilnehmerKursDto);

    List<TeilnehmerKursDto> toDtoList(List<TeilnehmerKurs> teilnehmerKurse);

    @Named("teilnehmerToFullName")
    default String teilnehmerToFullName(com.bildungsinsitut.deutschkurse.model.Teilnehmer teilnehmer) {
        if (teilnehmer == null) {
            return null;
        }
        return teilnehmer.getVorname() + " " + teilnehmer.getNachname();
    }
}